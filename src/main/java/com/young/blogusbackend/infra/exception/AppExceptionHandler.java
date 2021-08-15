package com.young.blogusbackend.infra.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value = { AccountExistsException.class })
    public ResponseEntity<Object> handleAccountExistsException(AccountExistsException ex, WebRequest request) {
        return new ResponseEntity<>(getErrorResponse(ex), new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }

        ValidationErrors validationErrors = new ValidationErrors(errors, now());
        return new ResponseEntity<>(validationErrors, new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleException(Exception ex, WebRequest webRequest) {
        return new ResponseEntity<>(getErrorResponse(ex), new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse getErrorResponse(Exception ex) {
        return new ErrorResponse(now(), ex.getMessage());
    }
}
