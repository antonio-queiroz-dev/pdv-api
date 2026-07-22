package com.pdvapi.sale;

import java.time.Instant;
import java.util.UUID;

public record SaleCancelResponse(
        UUID id,
        int code,
        SaleStatus status,
        Instant cancelledAt,
        int itemsRestored
) {
    public static SaleCancelResponse from(Sale sale) {
        return new SaleCancelResponse(
                sale.getId(),
                sale.getCode(),
                sale.getStatus(),
                sale.getCancelledAt(),
                sale.getItems().size()
        );
    }
}
