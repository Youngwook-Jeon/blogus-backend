package com.young.blogusbackend.infra.exception;

public class AccountExistsException extends RuntimeException {

    public AccountExistsException(String message) {
        super(message);
    }
}
