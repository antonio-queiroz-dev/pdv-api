package com.pdvapi.stock;

import java.time.Instant;
import java.util.UUID;

public record CancelResponse(
        CancelledMovementView cancelledMovement,
        ReversalMovementView reversalMovement
) {
    public record CancelledMovementView(
            UUID id,
            MovementType type,
            int quantity,
            boolean cancelled,
            Instant createdAt
    ) {
        public static CancelledMovementView from(StockMovement movement) {
            return new CancelledMovementView(
                    movement.getId(),
                    movement.getType(),
                    movement.getQuantity(),
                    movement.isCancelled(),
                    movement.getCreatedAt()
            );
        }
    }

    public record ReversalMovementView(
            UUID id,
            MovementType type,
            int quantity,
            UUID cancelledMovementId,
            String note,
            Instant createdAt
    ) {
        public static ReversalMovementView from(StockMovement movement) {
            return new ReversalMovementView(
                    movement.getId(),
                    movement.getType(),
                    movement.getQuantity(),
                    movement.getCancelledMovementId(),
                    movement.getNote(),
                    movement.getCreatedAt()
            );
        }
    }

    public static CancelResponse of(StockMovement original, StockMovement reversal) {
        return new CancelResponse(
                CancelledMovementView.from(original),
                ReversalMovementView.from(reversal)
        );
    }
}