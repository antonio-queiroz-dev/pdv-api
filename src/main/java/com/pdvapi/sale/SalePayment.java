package com.pdvapi.sale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "sale_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalePayment {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private Sale sale;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "amount_tendered", precision = 15, scale = 2)
    private BigDecimal amountTendered;

    public static SalePayment create(PaymentMethod paymentMethod, BigDecimal amount, BigDecimal amountTendered) {
        SalePayment payment = new SalePayment();
        payment.id = UUID.randomUUID();
        payment.paymentMethod = paymentMethod;
        payment.amount = amount;
        // amountTendered só se aplica a CASH
        payment.amountTendered = (paymentMethod == PaymentMethod.CASH) ? amountTendered : null;
        return payment;
    }
}
