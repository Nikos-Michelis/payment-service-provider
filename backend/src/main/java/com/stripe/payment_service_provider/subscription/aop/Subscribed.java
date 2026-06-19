package com.stripe.payment_service_provider.subscription.aop;

import com.stripe.payment_service_provider.subscription.model.PlanType;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribed {
    PlanType[] products() default {PlanType.TRIAL, PlanType.PRO};

    String expenseId();

    SubscriptionRule[] rules() default {};

    boolean useToken() default true;
}