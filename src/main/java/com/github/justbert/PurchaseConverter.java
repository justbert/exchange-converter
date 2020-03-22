package com.github.justbert;

import java.time.LocalDate;
import java.util.Currency;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PurchaseConverter {

    private static final Pattern CURRENCY_PATTERN = Pattern.compile("([A-Z]{3})");
    private static final Pattern COST_PATTERN = Pattern.compile("(\\d+\\.\\d+)");

    public static Purchase getAsPurchase(PaymentData paymentData) {
        return getPurchase(paymentData.getDate(), paymentData.getCost());
    }

    public static Purchase getPurchase(String date, String cost) {

        Matcher currencyMatcher = CURRENCY_PATTERN.matcher(cost);
        if (!currencyMatcher.find()) {
            throw new RuntimeException("Unable to determine currency");
        }

        Matcher costMatcher = COST_PATTERN.matcher(cost);
        if (!costMatcher.find()) {
            throw new RuntimeException("Unable to determine cost");
        }

        return Purchase.builder()
                .cost(Float.parseFloat(costMatcher.group()))
                .currency(Currency.getInstance(currencyMatcher.group()))
                .date(LocalDate.parse(date))
                .build();
    }
}
