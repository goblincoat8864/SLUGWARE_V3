package edu.utsa.cs3443.boxinggymapp.service.Impl;

import com.stripe.model.Subscription;
import com.stripe.param.SubscriptionCreateParams;
import edu.utsa.cs3443.boxinggymapp.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Value("${stripe.api.key}")
    private String secretKey;

    @Override
    public Subscription createSubscription(String customerId, String priceId) throws Exception {
        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(SubscriptionCreateParams.Item.builder().setPrice(priceId).build())
                .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                .build();
        return Subscription.create(params);
    }
}
