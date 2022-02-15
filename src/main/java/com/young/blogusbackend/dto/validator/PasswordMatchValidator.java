package com.young.blogusbackend.dto.validator;

import com.young.blogusbackend.dto.RegisterRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegisterRequest> {

    @Override
    public boolean isValid(
            RegisterRequest registerRequest,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        return Objects.equals(registerRequest.getPassword(), registerRequest.getCfPassword());
    }
}
