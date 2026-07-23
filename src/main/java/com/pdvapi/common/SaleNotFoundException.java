package com.pdvapi.common;

import java.util.UUID;

public class SaleNotFoundException extends RuntimeException {
    public SaleNotFoundException(UUID id) {
        super("Sale not found: " + id);
    }
}
