package com.stripe.payment_service_provider.payment.util;

import com.stripe.payment_service_provider.payment.repository.StripeCustomerRepository;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.user.model.User;
import com.stripe.payment_service_provider.user.reporitory.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerUtil {
    private final UserRepository userRepository;
    private final StripeCustomerRepository stripeCustomerRepository;

    public Optional<Customer> findCustomerByCustomerId(String customerId) throws StripeException {
        return Optional.ofNullable(Customer.retrieve(customerId));
    }

    public Optional<Customer> findCustomerByEmail(String email) throws StripeException {
        CustomerSearchParams params =
                CustomerSearchParams
                        .builder()
                        .setQuery("email:'" + email + "'")
                        .build();

        CustomerSearchResult result = Customer.search(params);

        return result.getData().stream().findFirst();
    }

    @Transactional
    public StripeCustomer findOrCreateStripeCustomer(String email) throws StripeException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<Customer> customer = findCustomerByEmail(user.getEmail());

        if (customer.isEmpty()) {
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder().setEmail(email).build();
            Customer newCustomer = Customer.create(customerCreateParams);
            StripeCustomer stripeCustomer = getStripeCustomer(newCustomer, user);
            return stripeCustomerRepository.save(stripeCustomer);
        }

        if (user.getStripeCustomer() == null) {
            StripeCustomer stripeCustomer = getStripeCustomer(customer.get(), user);
            return stripeCustomerRepository.save(stripeCustomer);
        }

        String existingCustomerId = user.getStripeCustomer().getStripeCustomerId();
        String retrievedCustomerId = customer.get().getId();
        if (!retrievedCustomerId.equals(existingCustomerId)) {
            throw new UsernameNotFoundException("Customer Stripe ID mismatch");
        }

        return user.getStripeCustomer();
    }

    private StripeCustomer getStripeCustomer(Customer customer, User user) {
        return StripeCustomer.builder()
                        .stripeCustomerId(customer.getId())
                        .user(user)
                        .email(customer.getEmail())
                        .build();
    }
}