package com.young.blogusbackend.modules.user.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.young.blogusbackend.infra.constant.UserConstant.EMAIL_REGEX;
import static com.young.blogusbackend.infra.constant.UserConstant.PHONE_REGEX;

public class ValidAccountValidator implements ConstraintValidator<ValidAccount, String> {

    @Override
    public boolean isValid(String account, ConstraintValidatorContext constraintValidatorContext) {
        return (validPhone(account) || validEmail(account));
    }

    public static boolean validPhone(String account) {
        String regExp = PHONE_REGEX;
        return account.trim().matches(regExp);
    }

    public static boolean validEmail(String account) {
        String regExp = EMAIL_REGEX;
        return account.trim().matches(regExp);
    }

}
