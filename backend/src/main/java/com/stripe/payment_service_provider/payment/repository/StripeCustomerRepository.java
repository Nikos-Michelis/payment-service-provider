package com.stripe.payment_service_provider.payment.repository;

import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StripeCustomerRepository extends JpaRepository<StripeCustomer, String> {
    Optional<StripeCustomer> findStripeCustomerByStripeCustomerId(String stripeCustomerId);
    Optional<StripeCustomer> findStripeCustomerByEmail(String email);
}
