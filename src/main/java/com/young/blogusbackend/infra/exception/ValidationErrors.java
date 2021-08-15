package com.young.blogusbackend.infra.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor @AllArgsConstructor
public class ValidationErrors {

    private Map<String, String> errors;
    private LocalDateTime timestamp;
}
