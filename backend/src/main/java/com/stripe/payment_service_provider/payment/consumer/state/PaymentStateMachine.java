package com.stripe.payment_service_provider.payment.consumer.state;

import com.stripe.payment_service_provider.payment.api.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.stripe.payment_service_provider.payment.api.model.PaymentStatus.*;

@Component
public class PaymentStateMachine extends BaseStateMachine<PaymentStatus> {

    @Override
    protected Set<PaymentStatus> validInitialStates() {
        return Set.of(PaymentStatus.CREATED);
    }

    @Override
    protected Map<PaymentStatus, Set<PaymentStatus>> validTransitions() {
        return Map.of(
                CREATED,             Set.of(PROCESSING, REQUIRES_ACTION, CAPTURED, FAILED, CANCELED),
                PROCESSING,          Set.of(REQUIRES_ACTION, CAPTURED, FAILED, CANCELED),
                REQUIRES_ACTION,     Set.of(PROCESSING, CAPTURED, FAILED, CANCELED),
                CAPTURED,            Set.of(DISPUTED, REFUND_PENDING, PARTIALLY_REFUNDED, REFUNDED),
                REFUND_PENDING,      Set.of(PARTIALLY_REFUNDED, REFUNDED, CAPTURED), // refund can fail, reverts
                PARTIALLY_REFUNDED,  Set.of(REFUND_PENDING, REFUNDED),               // can refund more
                DISPUTED,            Set.of(CAPTURED, REFUNDED),                     // dispute won/lost
                FAILED,              Set.of(),   // terminal
                CANCELED,            Set.of(),   // terminal
                REFUNDED,            Set.of()    // terminal
        );
    }
}