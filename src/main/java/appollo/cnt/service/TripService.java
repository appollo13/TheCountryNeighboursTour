package appollo.cnt.service;

import appollo.cnt.model.CountryResponse;
import appollo.cnt.model.TripPlanResponse;
import appollo.cnt.model.TripPlanResponse.NeighborCountry;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripService {

    private final CountryService countryService;
    private final ExchangeRatesService exchangeRatesService;

    @Autowired
    public TripService(CountryService countryService, ExchangeRatesService exchangeRatesService) {
        this.countryService = countryService;
        this.exchangeRatesService = exchangeRatesService;
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

        // the calculations
        int budgetPerTrip = neighbourCountriesCount * budgetPerCountry;
        int numberOfTrips = 0;
        if (neighbourCountriesCount != 0) { // corner case for island countries
            numberOfTrips = totalBudget / budgetPerTrip;
        }
        int leftoverBudget = totalBudget - (numberOfTrips * budgetPerTrip);
        int totalBudgetPerCountry = budgetPerCountry * numberOfTrips;
        List<NeighborCountry> budget = exchangeRatesService
            .calculateBudgetInLocalCurrencies(totalBudgetPerCountry, inputCurrency, neighborCountries);

        // the response
        return TripPlanResponse.builder()
            .startingCountry(startingCountry.getCountry())
            .budgetPerCountry(budgetPerCountry)
            .totalBudget(totalBudget)
            .inputCurrency(inputCurrency)
            .numberOfTrips(numberOfTrips)
            .leftoverBudget(leftoverBudget)
            .neighborCountries(budget)
            .build();
    }
}
