package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor @AllArgsConstructor
public class ValidationErrors {

    private Map<String, String> msg;
}
