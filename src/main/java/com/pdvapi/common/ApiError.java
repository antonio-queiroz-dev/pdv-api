package com.pdvapi.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(String message, Map<String, String> errors) {

    public static ApiError of(String message) {
        return new ApiError(message, null);
    }

    public static ApiError validation(Map<String, String> errors) {
        return new ApiError("Validation failed", errors);
    }
}
