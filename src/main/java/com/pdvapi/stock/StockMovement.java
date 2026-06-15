package com.pdvapi.stock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_movement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockMovement {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType type;

    @Column(nullable = false)
    private int quantity;

    @Column(length = 500)
    private String note;

    @Column(name = "operator_id", nullable = false)
    private UUID operatorId;

    @Column(nullable = false)
    private boolean cancelled;

    @Column(name = "cancelled_movement_id")
    private UUID cancelledMovementId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private StockMovement(UUID id, UUID tenantId, UUID productId, MovementType type, int quantity,
                          String note, UUID operatorId, UUID cancelledMovementId, Instant now) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.type = type;
        this.quantity = quantity;
        this.note = note;
        this.operatorId = operatorId;
        this.cancelled = false;
        this.cancelledMovementId = cancelledMovementId;
        this.createdAt = now;
    }

    public static StockMovement create(UUID tenantId, UUID productId, MovementType type, int quantity,
                                       String note, UUID operatorId) {
        return new StockMovement(UUID.randomUUID(), tenantId, productId, type, quantity,
                note, operatorId, null, Instant.now());
    }

    public static StockMovement reversal(UUID tenantId, UUID productId, UUID originalId,
                                         int reversedQuantity, UUID operatorId) {
        return new StockMovement(UUID.randomUUID(), tenantId, productId, MovementType.CANCELLATION,
                reversedQuantity, "Cancellation of movement " + originalId, operatorId, originalId, Instant.now());
    }

    public void markCancelled() {
        this.cancelled = true;
    }
}