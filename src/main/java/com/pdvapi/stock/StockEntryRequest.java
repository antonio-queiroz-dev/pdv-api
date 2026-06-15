package com.pdvapi.stock;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StockEntryRequest(
        @NotNull MovementType type,
        String note,
        @NotEmpty @Valid List<StockItemRequest> items
) {
}