package appollo.cnt.service;

import appollo.cnt.client.CountriesClient;
import appollo.cnt.model.CountryResponse;
import appollo.cnt.model.TripPlanResponse;
import appollo.cnt.model.TripPlanResponse.Budget;
import appollo.cnt.model.TripPlanResponse.NeighborCountry;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TripService {

    private final ObjectMapper objectMapper;
    private final CountriesClient countriesClient;

    @Autowired
    public TripService(ObjectMapper objectMapper, CountriesClient countriesClient) {
        this.objectMapper = objectMapper;
        this.countriesClient = countriesClient;
    }

    /* TODO the plan is as follows:
        2. get the currencies in each country
        3. get the needed exchange rates
     */
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

        // the calculations
        int budgetPerRoundRound = neighbourCountriesCount * budgetPerCountry;
        int completeRoundTrips = totalBudget / budgetPerRoundRound;
        int leftoverBudget = totalBudget - (completeRoundTrips * budgetPerRoundRound);

        // the response
        List<NeighborCountry> neighborCountriesWithBudget = new ArrayList<>(neighbourCountriesCount);
        for (CountryResponse neighbor : neighborCountries) {
            NeighborCountry neighborCountry = new NeighborCountry(neighbor);
            List<Budget> budgets = neighbor.getCurrencies().stream()
                .map(currency -> new Budget(budgetPerCountry, currency.getCode()))
                .collect(Collectors.toList());
            neighborCountry.setBudgets(budgets);
            neighborCountriesWithBudget.add(neighborCountry);
        }
        return TripPlanResponse.builder()
            .startingCountry(startingCountry)
            .budgetPerCountry(budgetPerCountry)
            .totalBudget(totalBudget)
            .inputCurrency(inputCurrency)
            .roundTrips(completeRoundTrips)
            .leftoverBudget(leftoverBudget)
            .neighborCountries(neighborCountriesWithBudget)
            .build();
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
