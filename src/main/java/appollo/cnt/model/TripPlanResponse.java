package appollo.cnt.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Data
@Builder
public class TripPlanResponse {

    private CountryNameAndCodes startingCountry;
    private int budgetPerCountry;
    private int totalBudget;
    private String inputCurrency;

    private int roundTrips;
    private int leftoverBudget;
    private List<NeighborCountry> neighborCountries;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class NeighborCountry extends CountryNameAndCodes {

        private List<Budget> budgets;

        public NeighborCountry(CountryNameAndCodes countryNameAndCodes) {
            setName(countryNameAndCodes.getName());
            setAlpha2Code(countryNameAndCodes.getAlpha2Code());
            setAlpha3Code(countryNameAndCodes.getAlpha3Code());
        }
    }

    @Value
    public static class Budget {

        int amount;
        String currency;
    }
}
