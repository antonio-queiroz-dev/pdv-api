package com.pdvapi.sale;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SaleSummaryResponse(
        UUID id,
        int code,
        BigDecimal finalAmount,
        SaleStatus status,
        int itemCount,
        String operatorName,
        Instant createdAt
) {
    public static SaleSummaryResponse from(Sale sale, String operatorName) {
        return new SaleSummaryResponse(
                sale.getId(),
                sale.getCode(),
                sale.getFinalAmount(),
                sale.getStatus(),
                sale.getItems().size(),
                operatorName,
                sale.getCreatedAt()
        );
    }
}
