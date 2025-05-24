package edu.utsa.cs3443.boxinggymapp.service;

import com.stripe.model.Subscription;

public interface SubscriptionService {
    Subscription createSubscription(String customerId, String priceId) throws Exception;
}
