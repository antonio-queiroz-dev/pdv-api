package com.pdvapi.common;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID id) {
        super("Category not found: " + id);
    }
}