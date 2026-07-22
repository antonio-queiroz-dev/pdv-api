package com.pdvapi.sale;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record SaleItemRequest(
        @NotNull UUID productId,
        @Positive int quantity
) {}
