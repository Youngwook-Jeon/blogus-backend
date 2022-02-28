package com.young.blogusbackend.exception;

import com.young.blogusbackend.dto.ErrorResponse;
import com.young.blogusbackend.dto.ValidationErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AppExceptionHandler {

    public static final String ACCESS_NOT_PERMITED = "접근 권한이 없습니다.";

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public ResponseEntity<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest webRequest
    ) {
        Map<String, String> errors = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String name = error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            if (error instanceof FieldError) {
                name = ((FieldError) error).getField();
            }

            errors.put(name, errorMessage);
        }

        ValidationErrors validationErrors = new ValidationErrors(errors);
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest webRequest) {
        return new ResponseEntity<>(getErrorResponse(ACCESS_NOT_PERMITED), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = { SpringBlogusException.class })
    public ResponseEntity<Object> handleSpringBlogusException(SpringBlogusException ex, WebRequest webRequest) {
        return new ResponseEntity<>(getErrorResponse(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleException(Exception ex, WebRequest webRequest) {
        return new ResponseEntity<>(getErrorResponse(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse getErrorResponse(Exception ex) {
        return new ErrorResponse(ex.getMessage());
    }

    private ErrorResponse getErrorResponse(String msg) {
        return new ErrorResponse(msg);
    }
}
