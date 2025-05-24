package edu.utsa.cs3443.boxinggymapp.service;

import edu.utsa.cs3443.boxinggymapp.dto.PaymentResponse;

public interface StripeService {
    PaymentResponse createPaymentIntent(Double amount);
    void handlePaymentWebhook(String payload, String sigHeader);
    PaymentResponse createSubscription(Integer gymOwnerId);
    String createStripeAccount(Integer gymOwnerId);
}
