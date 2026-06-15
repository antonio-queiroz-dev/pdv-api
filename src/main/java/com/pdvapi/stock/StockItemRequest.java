package com.pdvapi.stock;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record StockItemRequest(
        @NotNull UUID productId,
        @NotNull @Positive Integer quantity
) {
}