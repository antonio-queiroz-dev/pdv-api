package com.pdvapi.product;

import com.pdvapi.category.Category;
import com.pdvapi.unit.Unit;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummary(
        UUID id,
        int code,
        String name,
        BigDecimal sellingPrice,
        CategoryRef category,
        UnitRef unit,
        boolean active
) {
    public static ProductSummary from(Product product, Category category, Unit unit) {
        CategoryRef categoryRef = category != null ? new CategoryRef(category.getId(), category.getName()) : null;
        UnitRef unitRef = unit != null ? new UnitRef(unit.getId(), unit.getName()) : null;

        return new ProductSummary(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getSellingPrice(),
                categoryRef,
                unitRef,
                product.isActive()
        );
    }
}
