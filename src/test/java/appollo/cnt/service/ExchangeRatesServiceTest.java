package appollo.cnt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import appollo.cnt.client.ExchangeRatesClient;
import appollo.cnt.model.CountryNameAndCodes;
import appollo.cnt.model.CountryResponse;
import appollo.cnt.model.CountryResponse.Currency;
import appollo.cnt.model.ExchangeRatesResponse;
import appollo.cnt.model.TripPlanResponse.NeighborCountry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExchangeRatesServiceTest {

    @Mock
    private ExchangeRatesClient exchangeRatesClient;

    @Spy
    @InjectMocks
    private ExchangeRatesService exchangeRatesService;

    private CountryResponse createCountryResponse(String countryName, String currencyA, String currencyB) {
        CountryResponse countryResponse = new CountryResponse();

        CountryNameAndCodes name = new CountryNameAndCodes();
        name.setName(countryName);
        countryResponse.setCountry(name);

        List<Currency> currencies = new LinkedList<>();
        Currency cA = new Currency();
        cA.setCode(currencyA);
        currencies.add(cA);
        if (currencyB != null) {
            Currency cB = new Currency();
            cB.setCode(currencyB);
            currencies.add(cB);
        }
        countryResponse.setCurrencies(currencies);

        return countryResponse;
    }

    @Test
    public void givenTotalBudgetPerCountryIsZero_whenCalculateBudgetInLocalCurrencies_thenEmptyResult() {
        // given
        int totalBudgetPerCountry = 0;
        String inputCurrency = "EUR";
        List<CountryResponse> countries = Collections.emptyList();

        // when
        List<NeighborCountry> actual = exchangeRatesService
            .calculateBudgetInLocalCurrencies(totalBudgetPerCountry, inputCurrency, countries);

        // then
        assertTrue(actual.isEmpty());
        verify(exchangeRatesClient, never()).getExchangeRatesResponse(any());
    }

    @Test
    public void givenExchangeRatesResponseIsMissing_whenCalculateBudgetInLocalCurrencies_thenBudgetsInInitialCurrency() {
        // given
        int totalBudgetPerCountry = 13;
        String inputCurrency = "KPW";
        CountryResponse china = createCountryResponse("China", "CNY", "CNZ");
        CountryResponse sKorea = createCountryResponse("South Korea", "KRW", null);
        CountryResponse russia = createCountryResponse("Russia", "RUB", "EUR");
        List<CountryResponse> countries = Arrays.asList(china, sKorea, russia);
        when(exchangeRatesClient.getExchangeRatesResponse(inputCurrency)).thenReturn(null);

        // when
        List<NeighborCountry> actual = exchangeRatesService
            .calculateBudgetInLocalCurrencies(totalBudgetPerCountry, inputCurrency, countries);

        // then
        assertEquals(countries.size(), actual.size());
        for (NeighborCountry neighborCountry : actual) {
            assertEquals(1, neighborCountry.getBudget().size());
            checkBudget(totalBudgetPerCountry, neighborCountry, Collections.emptyMap(), inputCurrency);
        }
        verify(exchangeRatesClient, times(1)).getExchangeRatesResponse(inputCurrency);
    }

    @Test
    public void givenEverythingIsProvided_whenCalculateBudgetInLocalCurrencies_thenBudgetsInLocalCurrency() {
        // given
        double totalBudgetPerCountry = 13;
        String inputCurrency = "KPW";
        CountryResponse china = createCountryResponse("China", "CNY", "CNZ");
        CountryResponse sKorea = createCountryResponse("South Korea", "KRW", null);
        CountryResponse russia = createCountryResponse("Russia", "RUB", "EUR");
        List<CountryResponse> countries = Arrays.asList(china, sKorea, russia);
        ExchangeRatesResponse exchangeRatesResponse = new ExchangeRatesResponse();
        exchangeRatesResponse.setBase(inputCurrency);
        Map<String, Double> rates = new HashMap<>();
        rates.put("CNY", 1.1);
        rates.put("CNZ", 2.22);
        rates.put("RUB", 3.333);
        exchangeRatesResponse.setRates(rates);
        when(exchangeRatesClient.getExchangeRatesResponse(inputCurrency)).thenReturn(exchangeRatesResponse);

        // when
        List<NeighborCountry> actual = exchangeRatesService
            .calculateBudgetInLocalCurrencies((int) totalBudgetPerCountry, inputCurrency, countries);

        // then
        assertEquals(countries.size(), actual.size());
        for (NeighborCountry neighborCountry : actual) {
            switch (neighborCountry.getCountry().getName()) {
                case "China": {
                    assertEquals(2, neighborCountry.getBudget().size());
                    checkBudget(totalBudgetPerCountry, neighborCountry, rates, "CNY");
                    checkBudget(totalBudgetPerCountry, neighborCountry, rates, "CNZ");
                    break;
                }
                case "South Korea": {
                    assertEquals(1, neighborCountry.getBudget().size());
                    checkBudget(totalBudgetPerCountry, neighborCountry, rates, inputCurrency);
                    break;
                }
                case "Russia": {
                    assertEquals(1, neighborCountry.getBudget().size());
                    checkBudget(totalBudgetPerCountry, neighborCountry, rates, "RUB");
                    break;
                }
                default:
                    fail("Unexpected country: " + neighborCountry.getCountry());
            }
        }
        verify(exchangeRatesClient, times(1)).getExchangeRatesResponse(inputCurrency);
    }

    private void checkBudget(double totalBudgetPerCountry, NeighborCountry neighborCountry, Map<String, Double> rates,
        String currency) {
        assertTrue(neighborCountry.getBudget().containsKey(currency));
        Double rate = rates.getOrDefault(currency, 1d);
        assertEquals(totalBudgetPerCountry * rate, neighborCountry.getBudget().get(currency), 0.001);
    }
}
