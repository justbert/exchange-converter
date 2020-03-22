package com.github.justbert;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic service used to convert
 */
@Service
public class ExchangeRatesService {

    private ExchangeRatesRepository repository;
    private WebClient webClient = WebClient.builder()
            .baseUrl("https://api.exchangeratesapi.io/")
            .build();

    ExchangeRatesService(ExchangeRatesRepository repository) {
        this.repository = repository;
    }

    public DailyExchangeRates getExchangeRates(Currency base, LocalDate date, List<Currency> symbols) {
        DailyExchangeRates currentDailyExchangeRates = repository.getExchangeRates(base, date);

        if (currentDailyExchangeRates == null) {
            currentDailyExchangeRates = getExchangeRates(base, date);
        }

        return DailyExchangeRates.builder()
                .base(currentDailyExchangeRates.getBase())
                .date(currentDailyExchangeRates.getDate())
                .exchangeRates(getSpecifiedCurrencies(currentDailyExchangeRates.getExchangeRates(), symbols))
                .build();
    }

    private Map<Currency, Float> getSpecifiedCurrencies(Map<Currency, Float> ratesMap, List<Currency> symbols) {
        Map<Currency, Float> specifiedMap = new HashMap<>();
        symbols.forEach(symbol -> specifiedMap.put(symbol, ratesMap.get(symbol)));
        return specifiedMap;
    }

    public DailyExchangeRates getExchangeRates(Currency base, LocalDate date) {
        DailyExchangeRates currentDailyExchangeRates = repository.getExchangeRates(base, date);

        if (currentDailyExchangeRates == null) {
            currentDailyExchangeRates = webClient.get().uri(uriBuilder -> uriBuilder
                    .path("/" + date.toString())
                    .queryParam("base", base)
                    .build()
            ).retrieve()
                    .bodyToMono(DailyExchangeRates.class)
                    .block();
            repository.addExchangeRates(currentDailyExchangeRates);
            return currentDailyExchangeRates;
        }

        return currentDailyExchangeRates;
    }
}
