package com.pdvapi.stock;

import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        UUID productId,
        String productName,
        MovementType type,
        int quantity,
        String note,
        Instant createdAt
) {
    public static StockMovementResponse from(StockMovement movement, String productName) {
        return new StockMovementResponse(
                movement.getId(),
                movement.getProductId(),
                productName,
                movement.getType(),
                movement.getQuantity(),
                movement.getNote(),
                movement.getCreatedAt()
        );
    }
}