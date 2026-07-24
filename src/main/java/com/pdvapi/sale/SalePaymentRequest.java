package com.pdvapi.sale;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SalePaymentRequest(
        @NotNull PaymentMethod paymentMethod,
        @Positive BigDecimal amount,
        BigDecimal amountTendered
) {}
