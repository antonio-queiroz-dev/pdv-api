package com.pdvapi.common;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName, int currentStock) {
        super("Insufficient stock for product '" + productName + "': current stock is " + currentStock);
    }
}