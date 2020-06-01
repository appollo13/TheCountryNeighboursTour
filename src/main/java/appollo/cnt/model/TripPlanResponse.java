package appollo.cnt.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TripPlanResponse {

    CountryNameAndCodes startingCountry;
    int budgetPerCountry;
    int totalBudget;
    String inputCurrency;

    int numberOfTrips;
    int leftoverBudget;
    List<NeighborCountry> neighborCountries;

    @Value
    public static class NeighborCountry {

        @JsonUnwrapped
        CountryNameAndCodes country;
        Map<String, Double> budget;
    }
}
