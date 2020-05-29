package appollo.cnt.service;

import appollo.cnt.client.CountriesClient;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TripService {

    private final CountriesClient countriesClient;
    private final ExchangeRatesClient exchangeRatesClient;

    @Autowired
    public TripService(CountriesClient countriesClient, ExchangeRatesClient exchangeRatesClient) {
        this.countriesClient = countriesClient;
        this.exchangeRatesClient = exchangeRatesClient;
    }

    public TripPlanResponse planATrip(String startingCountryName, int budgetPerCountry, int totalBudget,
        String inputCurrency) {

        // input validation and conversions
        CountryResponse startingCountry = resolveCountryByName(startingCountryName);
        if (inputCurrency == null) {
            inputCurrency = startingCountry.getCurrencies().get(0).getCode();
        }
        int neighbourCountriesCount = startingCountry.getBorders().size();
        List<CountryResponse> neighborCountries = new ArrayList<>(neighbourCountriesCount);
        for (String neighborCountryCode : startingCountry.getBorders()) {
            neighborCountries.add(resolveCountryByCode(neighborCountryCode));
        }
        ExchangeRatesResponse exchangeRatesResponse = exchangeRatesClient.getCountryByName(inputCurrency);

        // the calculations
        int budgetPerRoundRound = neighbourCountriesCount * budgetPerCountry;
        int completeRoundTrips = totalBudget / budgetPerRoundRound;
        int leftoverBudget = totalBudget - (completeRoundTrips * budgetPerRoundRound);
        int totalBudgetPerCountry = budgetPerCountry * completeRoundTrips;
        List<NeighborCountry> budgets = calculateLocalBudgets(totalBudgetPerCountry, inputCurrency, neighborCountries,
            exchangeRatesResponse);

        // the response
        return TripPlanResponse.builder()
            .startingCountry(startingCountry.getCountryNameAndCodesOnly())
            .budgetPerCountry(budgetPerCountry)
            .totalBudget(totalBudget)
            .inputCurrency(inputCurrency)
            .roundTrips(completeRoundTrips)
            .leftoverBudget(leftoverBudget)
            .neighborCountries(budgets)
            .build();
    }

    private List<NeighborCountry> calculateLocalBudgets(int totalBudgetPerCountry, String inputCurrency,
        List<CountryResponse> neighborCountries, ExchangeRatesResponse exchangeRatesResponse) {
        List<NeighborCountry> neighborCountriesWithBudget = new ArrayList<>(neighborCountries.size());
        for (CountryResponse neighbor : neighborCountries) {
            NeighborCountry neighborCountry = new NeighborCountry(neighbor);
            List<Budget> budgets = neighbor.getCurrencies().stream()
                .map(currency -> getBudget(totalBudgetPerCountry, inputCurrency, exchangeRatesResponse, currency))
                .collect(Collectors.toList());
            neighborCountry.setBudgets(budgets);
            neighborCountriesWithBudget.add(neighborCountry);
        }
        return neighborCountriesWithBudget;
    }

    private Budget getBudget(int totalBudgetPerCountry, String inputCurrency,
        ExchangeRatesResponse exchangeRatesResponse, Currency currency) {
        Double rate = null;
        if (exchangeRatesResponse != null) {
            rate = exchangeRatesResponse.getRates().get(currency.getCode());
        }
        if (rate == null) {
            return new Budget(totalBudgetPerCountry, inputCurrency);
        }
        return new Budget((int) (totalBudgetPerCountry * rate), currency.getCode());
    }

    private CountryResponse resolveCountryByName(String name) {
        List<CountryResponse> countriesByName = countriesClient.getCountryByName(name);
        if (countriesByName == null || countriesByName.isEmpty()) {
            return resolveCountryByCode(name);
        }
        if (countriesByName.size() > 1) {
            throw new RuntimeException(HttpStatus.CONFLICT.toString());
        }
        return countriesByName.get(0);
    }

    private CountryResponse resolveCountryByCode(String code) {
        CountryResponse country = countriesClient.getCountryByCode(code);
        if (country == null) {
            throw new RuntimeException(HttpStatus.NOT_FOUND.toString());
        }
        return country;
    }
}
