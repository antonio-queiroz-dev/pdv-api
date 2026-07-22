package com.pdvapi.sale;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sale")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sale {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private int code;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal discount;

    @Column(name = "final_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SaleStatus status;

    @Column(name = "operator_id", nullable = false)
    private UUID operatorId;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalePayment> payments = new ArrayList<>();

    private Sale(UUID id, UUID tenantId, int code, BigDecimal totalAmount, BigDecimal discount,
                 BigDecimal finalAmount, UUID operatorId, Instant now) {
        this.id = id;
        this.tenantId = tenantId;
        this.code = code;
        this.totalAmount = totalAmount;
        this.discount = discount;
        this.finalAmount = finalAmount;
        this.status = SaleStatus.COMPLETED;
        this.operatorId = operatorId;
        this.createdAt = now;
    }

    public static Sale create(UUID tenantId, int code, BigDecimal totalAmount,
                              BigDecimal discount, UUID operatorId) {
        BigDecimal finalAmount = totalAmount.subtract(discount);
        return new Sale(UUID.randomUUID(), tenantId, code, totalAmount, discount,
                finalAmount, operatorId, Instant.now());
    }

    public void addItem(SaleItem item) {
        items.add(item);
        item.setSale(this);
    }

    public void addPayment(SalePayment payment) {
        payments.add(payment);
        payment.setSale(this);
    }

    public void cancel() {
        this.status = SaleStatus.CANCELLED;
        this.cancelledAt = Instant.now();
    }
}
