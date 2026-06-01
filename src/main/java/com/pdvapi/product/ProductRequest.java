package com.pdvapi.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 500) String description,
        @Size(max = 50) String barcode,
        @NotNull @DecimalMin(value = "0.01") BigDecimal sellingPrice,
        @DecimalMin(value = "0.01") BigDecimal costPrice,
        UUID categoryId,
        UUID unitId
) {
}
