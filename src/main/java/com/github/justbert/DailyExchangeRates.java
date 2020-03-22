package com.github.justbert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Currency;
import java.util.Map;

/**
 * Represents the exchange rates from a base currency to
 * other currencies on a certain date.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyExchangeRates {

    /**
     * The base currency.
     */
    private Currency base;

    /**
     * The rates of exchange into other currencies.
     */
    private Map<Currency, Float> exchangeRates;

    /**
     * The date to which the exchange rates are bound.
     */
    private LocalDate date;
}
