package com.pdvapi.common;

import java.math.BigDecimal;

public class InvalidAmountTenderedException extends RuntimeException {
    public InvalidAmountTenderedException(BigDecimal amountTendered, BigDecimal amount) {
        super("Amount tendered (" + amountTendered + ") must be greater than or equal to payment amount (" + amount + ")");
    }
}
