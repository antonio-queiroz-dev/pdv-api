package com.pdvapi.unit;

import java.util.UUID;

public record UnitSummary(
        UUID id,
        int code,
        String name,
        boolean active
) {
    public static UnitSummary from(Unit unit) {
        return new UnitSummary(unit.getId(), unit.getCode(), unit.getName(), unit.isActive());
    }
}
