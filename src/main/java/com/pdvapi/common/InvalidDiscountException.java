package com.pdvapi.common;

import java.math.BigDecimal;

public class InvalidDiscountException extends RuntimeException {
    public InvalidDiscountException(BigDecimal discount, BigDecimal totalAmount) {
        super("Discount (" + discount + ") cannot be greater than total amount (" + totalAmount + ")");
    }
}
