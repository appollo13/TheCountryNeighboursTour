package appollo.cnt.service;

import appollo.cnt.client.ExchangeRatesClient;
import appollo.cnt.model.CountryResponse;
import appollo.cnt.model.ExchangeRatesResponse;
import appollo.cnt.model.TripPlanResponse.NeighborCountry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExchangeRatesService {

    private final ExchangeRatesClient exchangeRatesClient;

    @Autowired
    public ExchangeRatesService(ExchangeRatesClient exchangeRatesClient) {
        this.exchangeRatesClient = exchangeRatesClient;
    }

    public List<NeighborCountry> calculateBudgetInLocalCurrencies(int totalBudgetPerCountry, String inputCurrency,
        List<CountryResponse> countries) {

        if (totalBudgetPerCountry == 0) { // kind of optimization
            return Collections.emptyList();
        }

        ExchangeRatesResponse exchangeRatesResponse = getExchangeRatesResponse(inputCurrency);

        return countries.stream()
            .map(neighbor -> {
                Map<String, Integer> budget = calculateBudgetInLocalCurrencies(totalBudgetPerCountry, inputCurrency,
                    exchangeRatesResponse, neighbor);
                return new NeighborCountry(neighbor.getCountry(), budget);
            })
            .collect(Collectors.toList());
    }

    private ExchangeRatesResponse getExchangeRatesResponse(String inputCurrency) {
        try {
            return exchangeRatesClient.getCountryByName(inputCurrency);
        } catch (Exception e) {
            log.warn("No ExchangeRates!", e);
            return null;
        }
    }

    private Map<String, Integer> calculateBudgetInLocalCurrencies(int totalBudgetPerCountry, String inputCurrency,
        ExchangeRatesResponse exchangeRatesResponse, CountryResponse neighbor) {

        Map<String, Integer> budgets = new HashMap<>(neighbor.getCurrencies().size());
        if (exchangeRatesResponse != null) {
            neighbor.getCurrencies().forEach(currency -> {
                Double rate = exchangeRatesResponse.getRates().get(currency.getCode());
                if (rate != null) {
                    budgets.put(currency.getCode(), (int) (totalBudgetPerCountry * rate));
                }
            });
        }

        if (budgets.isEmpty()) {
            log.debug("No ExchangeRates! Using inputCurrency.");
            budgets.put(inputCurrency, totalBudgetPerCountry);
        }
        return budgets;
    }
}