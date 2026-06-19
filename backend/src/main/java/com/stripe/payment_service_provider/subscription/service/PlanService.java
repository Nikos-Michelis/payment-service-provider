package com.stripe.payment_service_provider.subscription.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.payment_service_provider.subscription.dto.PlanDTO;

import java.util.List;

public interface PlanService {
    List<PlanDTO> getAllPlans();
    void createOrUpdatePrice(Price price) throws StripeException;
    void createOrUpdatePlan(Product product) throws StripeException;
    void deletePlan(Product product);
}
