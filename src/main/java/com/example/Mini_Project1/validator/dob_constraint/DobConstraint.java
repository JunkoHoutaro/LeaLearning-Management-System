package com.example.Mini_Project1.validator.dob_constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { MinAgeValidator.class }) // 1 annotation can be validate
                                                     // by many validators.
public @interface DobConstraint {
    // default
    String message() default "Age must be >= 18";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // custom
    int min(); // min age
}
