package com.pdvapi.stock;

import java.time.Instant;
import java.util.UUID;

public record StockBalanceResponse(
        UUID productId,
        String productName,
        int productCode,
        int currentStock,
        Instant lastMovementAt
) {
    // Construtor usado pela projeção JPQL: sum(quantity) vem como Long.
    public StockBalanceResponse(UUID productId, String productName, int productCode,
                                Long currentStock, Instant lastMovementAt) {
        this(productId, productName, productCode,
                currentStock == null ? 0 : currentStock.intValue(), lastMovementAt);
    }
}