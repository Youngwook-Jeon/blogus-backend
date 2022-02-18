package com.young.blogusbackend.exception;

import org.springframework.mail.MailException;

public class SpringBlogusException extends RuntimeException {

    public SpringBlogusException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }

    public SpringBlogusException(String exMessage) {
        super(exMessage);
    }
}
