package com.pdvapi.sale;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalePaymentResponse(
        PaymentMethod paymentMethod,
        BigDecimal amount,
        BigDecimal amountTendered,
        BigDecimal change
) {
    public static SalePaymentResponse from(SalePayment payment) {
        BigDecimal change = null;
        if (payment.getPaymentMethod() == PaymentMethod.CASH && payment.getAmountTendered() != null) {
            change = payment.getAmountTendered().subtract(payment.getAmount());
        }
        return new SalePaymentResponse(
                payment.getPaymentMethod(),
                payment.getAmount(),
                payment.getAmountTendered(),
                change
        );
    }
}
