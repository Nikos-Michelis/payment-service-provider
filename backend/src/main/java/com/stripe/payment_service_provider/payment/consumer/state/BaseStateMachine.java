package com.stripe.payment_service_provider.payment.consumer.state;

import com.stripe.payment_service_provider.payment.api.model.PaymentStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class BaseStateMachine<S> implements StateMachine<S> {
    protected abstract Set<PaymentStatus> validInitialStates();
    protected abstract Map<S, Set<S>> validTransitions();


    @Override
    public boolean canTransition(S from, S to) {
        return validTransitions()
            .getOrDefault(from, Set.of())
            .contains(to);
    }

    @Override
    public void transition(S from, S to) {
        if (!canTransition(from, to)) {
            log.warn("Invalid transition [{}] → [{}] ignored", from, to);
            return;
        }
        log.info("Transition [{}] → [{}]", from, to);
    }
}