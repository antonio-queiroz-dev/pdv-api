package com.pdvapi.category;

import java.util.UUID;

public record CategorySummary(
        UUID id,
        int code,
        String name,
        boolean active
) {
    public static CategorySummary from(Category category) {
        return new CategorySummary(category.getId(), category.getCode(), category.getName(), category.isActive());
    }
}
