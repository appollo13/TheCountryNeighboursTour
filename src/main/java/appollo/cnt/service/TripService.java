package appollo.cnt.service;

import appollo.cnt.client.ExchangeRatesClient;
import appollo.cnt.model.CountryResponse;
import appollo.cnt.model.CountryResponse.Currency;
import appollo.cnt.model.ExchangeRatesResponse;
import appollo.cnt.model.TripPlanResponse;
import appollo.cnt.model.TripPlanResponse.Budget;
import appollo.cnt.model.TripPlanResponse.NeighborCountry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TripService {

    private final CountryService countryService;
    private final ExchangeRatesClient exchangeRatesClient;

    @Autowired
    public TripService(CountryService countryService, ExchangeRatesClient exchangeRatesClient) {
        this.countryService = countryService;
        this.exchangeRatesClient = exchangeRatesClient;
    }

    public TripPlanResponse planATrip(String startingCountryName, int budgetPerCountry, int totalBudget,
        String inputCurrency) {

        // input validation and conversions
        CountryResponse startingCountry = countryService.resolveCountryByName(startingCountryName);
        int neighbourCountriesCount = startingCountry.getBorders().size();
        List<CountryResponse> neighborCountries = new ArrayList<>(neighbourCountriesCount);
        for (String neighborCountryCode : startingCountry.getBorders()) {
            neighborCountries.add(countryService.resolveCountryByCode(neighborCountryCode));
        }

        if (inputCurrency == null) {
            inputCurrency = startingCountry.getCurrencies().get(0).getCode();
        }
        ExchangeRatesResponse exchangeRatesResponse;
        try {
            exchangeRatesResponse = exchangeRatesClient.getCountryByName(inputCurrency);
        } catch (Exception e) {
            log.warn("No ExchangeRates!", e);
            exchangeRatesResponse = null;
        }

        // the calculations
        int budgetPerTrip = neighbourCountriesCount * budgetPerCountry;
        int numberOfTrips = 0;
        if (neighbourCountriesCount != 0) { // corner case for island countries
            numberOfTrips = totalBudget / budgetPerTrip;
        }
        int leftoverBudget = totalBudget - (numberOfTrips * budgetPerTrip);
        int totalBudgetPerCountry = budgetPerCountry * numberOfTrips;
        List<NeighborCountry> budgets = calculateBudgetInLocalCurrencies(
            totalBudgetPerCountry, inputCurrency, neighborCountries, exchangeRatesResponse);

        // the response
        return TripPlanResponse.builder()
            .startingCountry(startingCountry.getCountryNameAndCodesOnly())
            .budgetPerCountry(budgetPerCountry)
            .totalBudget(totalBudget)
            .inputCurrency(inputCurrency)
            .roundTrips(numberOfTrips)
            .leftoverBudget(leftoverBudget)
            .neighborCountries(budgets)
            .build();
    }

    private List<NeighborCountry> calculateBudgetInLocalCurrencies(int totalBudgetPerCountry, String inputCurrency,
        List<CountryResponse> countries, ExchangeRatesResponse exchangeRatesResponse) {

        List<NeighborCountry> neighborCountries = new ArrayList<>(countries.size());
        for (CountryResponse neighbor : countries) {
            NeighborCountry neighborCountry = new NeighborCountry(neighbor);
            List<Budget> budgets = neighbor.getCurrencies().stream()
                .map(currency ->
                    getBudgetInLocalCurrency(totalBudgetPerCountry, inputCurrency, currency, exchangeRatesResponse))
                .collect(Collectors.toList());
            neighborCountry.setBudgets(budgets);
            neighborCountries.add(neighborCountry);
        }
        return neighborCountries;
    }

    private Budget getBudgetInLocalCurrency(int totalBudgetPerCountry, String inputCurrency,
        Currency currency, ExchangeRatesResponse exchangeRatesResponse) {

        Double rate = null;
        if (exchangeRatesResponse != null) {
            rate = exchangeRatesResponse.getRates().get(currency.getCode());
        }
        if (rate == null) {
            log.debug("No ExchangeRates! Using inputCurrency.");
            return new Budget(totalBudgetPerCountry, inputCurrency);
        }
        return new Budget((int) (totalBudgetPerCountry * rate), currency.getCode());
    }
}
