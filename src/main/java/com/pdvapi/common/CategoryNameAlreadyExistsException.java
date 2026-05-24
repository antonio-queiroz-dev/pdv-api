package com.pdvapi.common;

public class CategoryNameAlreadyExistsException extends RuntimeException {
    public CategoryNameAlreadyExistsException(String name) {
        super("Category name already exists: " + name);
    }
}