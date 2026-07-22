package com.pdvapi.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public record CreateSaleRequest(
        @NotEmpty List<@Valid SaleItemRequest> items,
        @PositiveOrZero BigDecimal discount,
        @NotEmpty List<@Valid SalePaymentRequest> payments
) {
    public CreateSaleRequest {
        if (discount == null) discount = BigDecimal.ZERO;
    }
}
