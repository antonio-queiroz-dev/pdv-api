package com.pdvapi.common;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID id) {
        super("Product not found: " + id);
    }

    public ProductNotFoundException(String barcode) {
        super("Product not found by barcode: " + barcode);
    }
}
