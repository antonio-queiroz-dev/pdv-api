package com.pdvapi.common;

public class UnitNameAlreadyExistsException extends RuntimeException {
    public UnitNameAlreadyExistsException(String name) {
        super("Unit name already exists: " + name);
    }
}
