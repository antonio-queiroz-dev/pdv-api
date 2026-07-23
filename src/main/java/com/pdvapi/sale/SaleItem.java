package com.pdvapi.sale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "sale_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaleItem {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private Sale sale;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 120)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    public static SaleItem create(UUID productId, String productName, int quantity, BigDecimal unitPrice) {
        SaleItem item = new SaleItem();
        item.id = UUID.randomUUID();
        item.productId = productId;
        item.productName = productName;
        item.quantity = quantity;
        item.unitPrice = unitPrice;
        item.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return item;
    }
}
