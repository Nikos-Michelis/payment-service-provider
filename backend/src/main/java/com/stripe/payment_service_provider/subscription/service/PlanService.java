package com.stripe.payment_service_provider.subscription.service;

import com.stripe.model.Event;
import com.stripe.payment_service_provider.subscription.dto.PlanDTO;

import java.util.List;

public interface PlanService {
    List<PlanDTO> getAllPlans();
    void onPlanCreate(Event event);
    void onPlanUpdate(Event event);
    void onPriceCreate(Event event);
    void onPriceUpdate(Event event);
}
