package com.pdvapi.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private int code;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String barcode;

    @Column(name = "selling_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "cost_price", precision = 15, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "unit_id")
    private UUID unitId;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private Product(UUID id, UUID tenantId, int code, String name, String description, String barcode,
                    BigDecimal sellingPrice, BigDecimal costPrice, UUID categoryId, UUID unitId, Instant now) {
        this.id = id;
        this.tenantId = tenantId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.sellingPrice = sellingPrice;
        this.costPrice = costPrice;
        this.categoryId = categoryId;
        this.unitId = unitId;
        this.active = true;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static Product create(UUID tenantId, int code, String name, String description, String barcode,
                                 BigDecimal sellingPrice, BigDecimal costPrice, UUID categoryId, UUID unitId) {
        return new Product(UUID.randomUUID(), tenantId, code, name, description, barcode,
                sellingPrice, costPrice, categoryId, unitId, Instant.now());
    }

    public void update(String name, String description, String barcode,
                       BigDecimal sellingPrice, BigDecimal costPrice, UUID categoryId, UUID unitId) {
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.sellingPrice = sellingPrice;
        this.costPrice = costPrice;
        this.categoryId = categoryId;
        this.unitId = unitId;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }
}
