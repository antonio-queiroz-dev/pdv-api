package com.pdvapi.category;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        int code,
        String name,
        boolean active,
        Instant createdAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.isActive(),
                category.getCreatedAt()
        );
    }
}