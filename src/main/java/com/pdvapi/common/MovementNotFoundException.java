package com.pdvapi.common;

import java.util.UUID;

public class MovementNotFoundException extends RuntimeException {
    public MovementNotFoundException(UUID id) {
        super("Movement not found: " + id);
    }
}