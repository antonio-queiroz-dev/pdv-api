package com.pdvapi.unit;

import java.time.Instant;
import java.util.UUID;

public record UnitResponse(
        UUID id,
        int code,
        String name,
        boolean active,
        Instant createdAt
) {
    public static UnitResponse from(Unit unit) {
        return new UnitResponse(
                unit.getId(),
                unit.getCode(),
                unit.getName(),
                unit.isActive(),
                unit.getCreatedAt()
        );
    }
}
