package edu.utsa.cs3443.boxinggymapp.controller;

import edu.utsa.cs3443.boxinggymapp.api.PaymentsApiDelegate;
import edu.utsa.cs3443.boxinggymapp.dto.CreateStripeAccount200Response;
import edu.utsa.cs3443.boxinggymapp.dto.PaymentResponse;
import edu.utsa.cs3443.boxinggymapp.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentsController implements PaymentsApiDelegate {

    private final StripeService stripeService;

    @Override
    public ResponseEntity<PaymentResponse> createPaymentIntent(Double amount) {
        return ResponseEntity.ok(stripeService.createPaymentIntent(amount));
    }

    @Override
    public ResponseEntity<Void> handleWebhook(String sigHeader, String payload) {
        stripeService.handlePaymentWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<PaymentResponse> createSubscription(Integer id){
        return ResponseEntity.ok(stripeService.createSubscription(id));
    }

    @Override
    public ResponseEntity<CreateStripeAccount200Response> createStripeAccount(Integer id){
        CreateStripeAccount200Response url = new CreateStripeAccount200Response();
        url.setUrl(stripeService.createStripeAccount(id));
        return ResponseEntity.ok(url);
    }
}


