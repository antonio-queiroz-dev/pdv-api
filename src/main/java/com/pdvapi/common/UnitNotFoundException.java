package com.pdvapi.common;

import java.util.UUID;

public class UnitNotFoundException extends RuntimeException {
    public UnitNotFoundException(UUID id) {
        super("Unit not found: " + id);
    }
}
