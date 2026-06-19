package com.stripe.payment_service_provider.security.validators;

import com.stripe.payment_service_provider.user.reporitory.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public record ExistsByEmailValidator(UserRepository userRepository)
        implements ConstraintValidator<ExistsByEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !userRepository.existsByEmail(email);
    }
}