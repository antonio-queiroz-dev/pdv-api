package com.pdvapi.sale;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
        UUID id,
        int code,
        List<SaleItemResponse> items,
        BigDecimal totalAmount,
        BigDecimal discount,
        BigDecimal finalAmount,
        List<SalePaymentResponse> payments,
        SaleStatus status,
        String operatorName,
        Instant createdAt
) {
    public static SaleResponse from(Sale sale, String operatorName) {
        List<SaleItemResponse> items = sale.getItems().stream()
                .map(SaleItemResponse::from)
                .toList();

        List<SalePaymentResponse> payments = sale.getPayments().stream()
                .map(SalePaymentResponse::from)
                .toList();

        return new SaleResponse(
                sale.getId(),
                sale.getCode(),
                items,
                sale.getTotalAmount(),
                sale.getDiscount(),
                sale.getFinalAmount(),
                payments,
                sale.getStatus(),
                operatorName,
                sale.getCreatedAt()
        );
    }
}
