package com.github.justbert;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * A persistence layer so that Rates can be cached to prevent overburdening the
 * API.
 */
@Service
public class ExchangeRatesRepository {

    private final Map<Currency, Map<LocalDate, DailyExchangeRates>> ratesStore = new HashMap<>();

    void addExchangeRates(DailyExchangeRates dailyExchangeRates) {
        final Map<LocalDate, DailyExchangeRates> currentBaseStore;
        if (ratesStore.containsKey(dailyExchangeRates.getBase())) {
            currentBaseStore = ratesStore.get(dailyExchangeRates.getBase());
        } else {
            currentBaseStore = new HashMap<>();
            ratesStore.put(dailyExchangeRates.getBase(), currentBaseStore);
        }
        currentBaseStore.put(dailyExchangeRates.getDate(), dailyExchangeRates);
    }

    DailyExchangeRates getExchangeRates(Currency base, LocalDate date) {
        Map<LocalDate, DailyExchangeRates> baseMap = ratesStore.get(base);
        if (baseMap != null) {
            return baseMap.get(date);
        }
        return null;
    }
}
