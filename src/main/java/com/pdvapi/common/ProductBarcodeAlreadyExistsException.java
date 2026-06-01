package com.pdvapi.common;

public class ProductBarcodeAlreadyExistsException extends RuntimeException {
    public ProductBarcodeAlreadyExistsException(String barcode) {
        super("Product barcode already exists: " + barcode);
    }
}
