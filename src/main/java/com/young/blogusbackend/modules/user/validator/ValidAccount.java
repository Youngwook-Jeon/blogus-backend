package com.young.blogusbackend.modules.user.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidAccountValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAccount {

    String message() default "유효하지 않은 계정 형식입니다.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
