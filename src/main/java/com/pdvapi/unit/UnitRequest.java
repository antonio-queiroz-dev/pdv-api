package com.pdvapi.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnitRequest(
        @NotBlank @Size(max = 120) String name
) {
}
