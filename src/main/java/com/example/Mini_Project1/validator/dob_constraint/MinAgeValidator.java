package com.example.Mini_Project1.validator.dob_constraint;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinAgeValidator implements ConstraintValidator<DobConstraint, LocalDate> {

    private int min;

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) { // best practice: 1 validator class is only used to validate 1 logic.
            return true;
        }
        if (ChronoUnit.YEARS.between(value, LocalDate.now()) >= min) { // check min age
            return true;
        }
        return false;
    }

    @Override
    public void initialize(DobConstraint dobConstraint) { // initialize min value from argument in @DobConstraint in
                                                          // SignUpRequest.java or any class where contains
                                                          // @DobConstraint annotation
        ConstraintValidator.super.initialize(dobConstraint);
        min = dobConstraint.min();
    }

}
