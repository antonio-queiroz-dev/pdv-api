package com.pdvapi.product;

import com.pdvapi.category.Category;
import com.pdvapi.unit.Unit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        int code,
        String name,
        String description,
        String barcode,
        BigDecimal sellingPrice,
        BigDecimal costPrice,
        BigDecimal profitMargin,
        BigDecimal markup,
        CategoryRef category,
        UnitRef unit,
        boolean active,
        Instant createdAt
) {
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public static ProductResponse from(Product product, Category category, Unit unit) {
        BigDecimal profitMargin = null;
        BigDecimal markup = null;
        if (product.getCostPrice() != null) {
            BigDecimal profit = product.getSellingPrice().subtract(product.getCostPrice());
            profitMargin = profit
                    .multiply(HUNDRED)
                    .divide(product.getSellingPrice(), 2, RoundingMode.HALF_UP);
            markup = profit
                    .multiply(HUNDRED)
                    .divide(product.getCostPrice(), 2, RoundingMode.HALF_UP);
        }

        CategoryRef categoryRef = category != null ? new CategoryRef(category.getId(), category.getName()) : null;
        UnitRef unitRef = unit != null ? new UnitRef(unit.getId(), unit.getName()) : null;

        return new ProductResponse(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getDescription(),
                product.getBarcode(),
                product.getSellingPrice(),
                product.getCostPrice(),
                profitMargin,
                markup,
                categoryRef,
                unitRef,
                product.isActive(),
                product.getCreatedAt()
        );
    }
}
