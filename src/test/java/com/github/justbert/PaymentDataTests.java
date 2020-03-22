package com.github.justbert;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentDataTests {

    @Test
    void first() {
        PaymentData paymentData = new PaymentData();

        paymentData.setDate("2012-01-04");
        paymentData.setCost("$49.99 USD");

        final Purchase expectedPurchase = Purchase.builder()
                .cost(49.99f)
                .date(LocalDate.parse("2012-01-04"))
                .currency(Currency.getInstance("USD"))
                .build();

        assertEquals(expectedPurchase, PurchaseConverter.getAsPurchase(paymentData));
    }

    @Test
    void second() {
        PaymentData paymentData = new PaymentData();

        paymentData.setDate("2012-01-04");
        paymentData.setCost("CAD$ 45.98");

        final Purchase expectedPurchase = Purchase.builder()
                .cost(45.98f)
                .date(LocalDate.parse("2012-01-04"))
                .currency(Currency.getInstance("CAD"))
                .build();

        assertEquals(expectedPurchase, PurchaseConverter.getAsPurchase(paymentData));
    }
}