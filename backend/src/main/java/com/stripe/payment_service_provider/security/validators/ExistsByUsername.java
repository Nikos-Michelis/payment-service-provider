package com.stripe.payment_service_provider.security.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsByUsernameValidator.class)
public @interface ExistsByUsername {
    String message() default "Username already taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
