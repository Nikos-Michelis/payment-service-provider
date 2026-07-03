package com.stripe.payment_service_provider.payment.api.repository;

import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<StripeCustomer, String> {
    Optional<StripeCustomer> findStripeCustomerByStripeCustomerId(String stripeCustomerId);
    Optional<StripeCustomer> findStripeCustomerByEmail(String email);
}
