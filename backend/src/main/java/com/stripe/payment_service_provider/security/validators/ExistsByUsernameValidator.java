package com.stripe.payment_service_provider.security.validators;

import com.stripe.payment_service_provider.user.reporitory.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public record ExistsByUsernameValidator(UserRepository userRepository)
        implements ConstraintValidator<ExistsByUsername, String> {

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return !userRepository.existsByUsername(username);
    }
}