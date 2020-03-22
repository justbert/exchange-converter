package com.github.justbert;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.nio.file.Files;
import java.util.Currency;
import java.util.List;

@Service
public class PurchaseCostConverter implements CommandLineRunner {

    private ExchangeRatesService exchangeRatesService;
    private ResourceLoader resourceLoader;

    PurchaseCostConverter(ExchangeRatesService exchangeRatesService,
                          ResourceLoader resourceLoader) {
        this.exchangeRatesService = exchangeRatesService;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Converts purchase data in a currency and converts it to another.
     * Takes three parameters:
     * <ul>
     *     <li>Location of a CSV of purchasing data containing an ISO formatted date(2020-03-22)
     *     and a cost with the ISO 4217 currency code(CAD).</li>
     *     <li>A location to output a new CSV</li>
     *     <li>An ISO 4217 currency code which determines what to convert to (CAD, USD, etc.)</li>
     * </ul>
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        Resource inputData = resourceLoader.getResource(args[0]);
        Resource outputData = resourceLoader.getResource(args[1]);
        Currency exchangeCurrency = Currency.getInstance(args[2]);

        if (!inputData.exists() && !inputData.isReadable()) {
            throw new RuntimeException("Unable to locate payment data");
        }

        CsvMapper readerMapper = new CsvMapper();

        try (CsvListWriter writer = new CsvListWriter(Files.newBufferedWriter(outputData.getFile().toPath()), CsvPreference.STANDARD_PREFERENCE);
             MappingIterator<PaymentData> mappingIterator = readerMapper.readerFor(PaymentData.class)
                     .with(CsvSchema.emptySchema().withHeader())
                     .readValues(inputData.getFile())) {
            writer.writeHeader("Date", "Cost", "Currency", "Cost in " + exchangeCurrency.toString());

            while (mappingIterator.hasNext()) {
                PaymentData currentPaymentData = mappingIterator.next();
                Purchase currentPurchase = PurchaseConverter.getAsPurchase(currentPaymentData);

                writer.write(currentPurchase.getDate(),
                        currentPurchase.getCost(),
                        currentPurchase.getCurrency(),
                        convertCost(exchangeCurrency, currentPurchase)
                );
            }
        }

        System.exit(0);
    }

    /**
     * Converts a purchase cost to the specified currency.
     *
     * @param exchangeCurrency the currency that the cost should be converted to
     * @param purchase         the purchase to convert
     * @return the cost in the specified currency
     */
    private float convertCost(Currency exchangeCurrency, Purchase purchase) {
        if (exchangeCurrency.equals(purchase.getCurrency())) {
            return purchase.getCost();
        }

        DailyExchangeRates dailyExchangeRates = exchangeRatesService.getExchangeRates(purchase.getCurrency(), purchase.getDate(), List.of(exchangeCurrency));

        return dailyExchangeRates.getExchangeRates().get(exchangeCurrency) * purchase.getCost();
    }
}
