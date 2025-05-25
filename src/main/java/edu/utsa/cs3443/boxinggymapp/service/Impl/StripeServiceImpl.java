package edu.utsa.cs3443.boxinggymapp.service.Impl;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import edu.utsa.cs3443.boxinggymapp.dto.PaymentResponse;
import edu.utsa.cs3443.boxinggymapp.model.GymMember;
import edu.utsa.cs3443.boxinggymapp.model.GymOwner;
import edu.utsa.cs3443.boxinggymapp.repository.GymMemberRepository;
import edu.utsa.cs3443.boxinggymapp.repository.GymOwnerRepository;
import edu.utsa.cs3443.boxinggymapp.service.StripeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeServiceImpl.class);

    @Value("${stripe.api.key}")
    private String secretKey;

    @Value("${stripe.webhook.key}")
    private String webhookKey;

    private final GymOwnerRepository gymOwnerRepository;

    private final GymMemberRepository gymMemberRepository;

    @Override
    public PaymentResponse createSubscription(Integer gymOwnerId){
        try{
            GymOwner gymOwner = gymOwnerRepository.findById(gymOwnerId)
                    .orElseThrow(() -> new RuntimeException("Gym owner not found"));

            String customerId = gymOwner.getStripeCustomerId();
            if (customerId == null) {
                customerId = createStripeCustomer(gymOwner);
                gymOwner.setStripeCustomerId(customerId);
            }
            String ephemeralKey = createEphemeralKey(customerId);

            Map<String, Object> subscriptionParams = new HashMap<>();

            subscriptionParams.put("customer", customerId);

            Map<String, Object> items = new HashMap<>();
            items.put("price", "price_1RGmFoP6B2ZwlaBLftMFYFGT");

            subscriptionParams.put("items", List.of(items));
            subscriptionParams.put("payment_behavior", "default_incomplete");

            List<String> expandList = new ArrayList<>();
            expandList.add("latest_invoice.payment_intent");

            subscriptionParams.put("expand", expandList);

            Subscription subscription = Subscription.create(subscriptionParams);
            gymOwner.setSubscriptionId(subscription.getId());

            // Return client secret for payment confirmation
            String clientSecret = subscription.getLatestInvoiceObject().getPaymentIntentObject().getClientSecret();

            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setClientSecret(clientSecret);
            paymentResponse.setCustomerId(customerId);
            paymentResponse.setEphemeralKey(ephemeralKey);

            return paymentResponse;
        } catch (StripeException e) {
            throw new RuntimeException("Erreur de paiement: " + e.getMessage());
        }
    }

    private String createStripeCustomer(GymOwner gymOwner) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("email", gymOwner.getEmail());
        params.put("name", gymOwner.getName());

        Customer customer = Customer.create(params);

        return customer.getId();
    }

    private String createEphemeralKey(String customerId) {
        try {
            EphemeralKeyCreateParams params = EphemeralKeyCreateParams.builder()
                    .setCustomer(customerId)
                    .setStripeVersion("2024-06-20")
                    .build();

            EphemeralKey ephemeralKey = EphemeralKey.create(params);

            return ephemeralKey.getSecret();

        } catch (StripeException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public String createStripeAccount(Integer gymOwnerId){
        try {
            GymOwner owner = gymOwnerRepository.findById(gymOwnerId)
                    .orElseThrow(() -> new RuntimeException("Gym owner not found"));

            AccountCreateParams params = AccountCreateParams.builder()
                    .setCountry("US")
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setEmail(owner.getEmail())
                    .build();

            Account account = Account.create(params);

            owner.setStripeAccountId(account.getId());

            AccountLink link = AccountLink.create(
                    AccountLinkCreateParams.builder()
                            .setAccount(account.getId())
                            .setRefreshUrl("https://google.com")
                            .setReturnUrl("https://google.com")
                            .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                            .build()
            );

            return link.getUrl();
        } catch (StripeException e){
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public PaymentResponse createPaymentIntent(Double amount) {
        try {
            String gymMemberUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            GymMember gymMember = gymMemberRepository.findByUsername(gymMemberUsername).orElseThrow(() -> new RuntimeException("Gym member not found"));

            GymOwner gymOwner = gymMember.getGymOwner();
            if (gymMember.getStripeCustomerId()==null){
                Customer customer = Customer.create(new HashMap<>());
                gymMember.setStripeCustomerId(customer.getId());
            }

            String ephemeralKey = createEphemeralKey(gymMember.getStripeCustomerId());

            Double amountInCents = amount * 100;
            Double stripeFee = amountInCents * 0.029 + 30;
            Double finalAmount = amountInCents + stripeFee;

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(finalAmount.longValue())
                    .setCurrency("usd")
                    .setCustomer(gymMember.getStripeCustomerId())
                    .setTransferData(PaymentIntentCreateParams.TransferData.builder()
                            .setDestination(gymOwner.getStripeAccountId())
                            .build())
//                    .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
//                            .setEnabled(true)
//                            .build())
                    .setDescription(amount.toString())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            PaymentResponse response = new PaymentResponse();
            response.setClientSecret(paymentIntent.getClientSecret());
            response.setEphemeralKey(ephemeralKey);
            response.setCustomerId(gymMember.getStripeCustomerId());

            return response;
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create payment session: " + e.getMessage(), e);
        }
    }

    @Override
    public void handlePaymentWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    webhookKey
            );

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
                    handlePaymentSuccess(paymentIntent);
                    break;
                case "payment_intent.payment_failed":
                    PaymentIntent failedIntent = (PaymentIntent) event.getData().getObject();
//                    handlePaymentFailure(failedIntent);
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur de webhook: " + e.getMessage());
        }
    }

    private void handlePaymentSuccess(PaymentIntent paymentIntent) {
        String customerId = paymentIntent.getCustomer();
        Optional<GymOwner> gymOwnerOptional = gymOwnerRepository.findByStripeCustomerId(customerId);

        if (gymOwnerOptional.isPresent()){
            GymOwner gymOwner = gymOwnerOptional.get();
            gymOwner.setPaid(true);
            gymOwner.setPaidUntil(LocalDateTime.now().plusMonths(1));
            gymOwner.setPaymentMethodId(paymentIntent.getPaymentMethod());
        } else {
            Optional<GymMember> gymMemberOptional = gymMemberRepository.findByStripeCustomerId(customerId);
            if (gymMemberOptional.isPresent()){
                GymMember gymMember = gymMemberOptional.get();
                gymMember.setBalance(gymMember.getBalance() + Double.parseDouble(paymentIntent.getDescription()));
                gymMember.setPaid(gymMember.getBalance() >= 0);
            }
        }


    }

    private void handlePaymentFailure(PaymentIntent paymentIntent) {
        String customerId = paymentIntent.getCustomer();
        GymOwner gymOwner = gymOwnerRepository.findByStripeCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Gym owner not found"));

        gymOwner.setPaid(false);
    }
}

