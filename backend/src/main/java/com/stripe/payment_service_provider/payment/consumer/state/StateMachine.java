package com.stripe.payment_service_provider.payment.consumer.state;

public interface StateMachine<S> {
    boolean canTransition(S from, S to);
    void transition(S from, S to);
}