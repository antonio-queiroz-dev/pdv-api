package com.pdvapi.stock;

import java.time.Instant;
import java.util.UUID;

public record MovementHistoryResponse(
        UUID id,
        MovementType type,
        int quantity,
        String note,
        boolean cancelled,
        String operatorName,
        Instant createdAt
) {
    public static MovementHistoryResponse from(StockMovement movement, String operatorName) {
        return new MovementHistoryResponse(
                movement.getId(),
                movement.getType(),
                movement.getQuantity(),
                movement.getNote(),
                movement.isCancelled(),
                operatorName,
                movement.getCreatedAt()
        );
    }
}