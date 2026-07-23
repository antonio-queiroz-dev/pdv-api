package com.pdvapi.common;

import java.math.BigDecimal;

public class PaymentMismatchException extends RuntimeException {
    public PaymentMismatchException(BigDecimal expected, BigDecimal received) {
        super("Payment total (" + received + ") does not match final amount (" + expected + ")");
    }
}
