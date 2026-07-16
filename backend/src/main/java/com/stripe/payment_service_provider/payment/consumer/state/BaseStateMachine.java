package com.stripe.payment_service_provider.payment.consumer.state;

import com.stripe.payment_service_provider.payment.api.model.PaymentStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class BaseStateMachine<T> implements StateMachine<T> {
    protected abstract Map<T, Set<T>> validTransitions();


    @Override
    public boolean canTransition(T from, T to) {
        return validTransitions()
            .getOrDefault(from, Set.of())
            .contains(to);
    }

    @Override
    public void transition(T from, T to) {
        if (!canTransition(from, to)) {
            log.warn("Invalid transition [{}] → [{}] ignored", from, to);
            return;
        }
        log.info("Transition [{}] → [{}]", from, to);
    }
}