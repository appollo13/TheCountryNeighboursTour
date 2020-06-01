package appollo.cnt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import appollo.cnt.model.CountryNameAndCodes;
import appollo.cnt.model.CountryResponse;
import appollo.cnt.model.CountryResponse.Currency;
import appollo.cnt.model.TripPlanResponse;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private CountryService countryService;
    @Mock
    private ExchangeRatesService exchangeRatesService;

    @Spy
    @InjectMocks
    private TripService tripService;

    @Test
    public void givenNeighbourCountriesCountIsZero_whenPlanATrip_thenEmptyResult() {
        // given
        String code = "MT";
        int budgetPerCountry = 1;
        int totalBudget = 13;
        String inputCurrency = "EUR";

        CountryResponse initialCountry = new CountryResponse();
        CountryNameAndCodes initialCountryNames = new CountryNameAndCodes();
        initialCountryNames.setName("Malta");
        initialCountryNames.setAlpha2Code(code);
        initialCountry.setCountry(initialCountryNames);
        Currency currency = new Currency();
        currency.setCode(inputCurrency);
        initialCountry.setCurrencies(Collections.singletonList(currency));
        initialCountry.setBorders(Collections.emptyList());
        when(countryService.resolveCountryByName(code)).thenReturn(initialCountry);

        // when
        TripPlanResponse actualTripPlan = tripService.planATrip(code, budgetPerCountry, totalBudget, inputCurrency);

        // then
        assertEquals(initialCountryNames, actualTripPlan.getStartingCountry());
        assertEquals(budgetPerCountry, actualTripPlan.getBudgetPerCountry());
        assertEquals(totalBudget, actualTripPlan.getTotalBudget());
        assertEquals(inputCurrency, actualTripPlan.getInputCurrency());
        assertEquals(0, actualTripPlan.getNumberOfTrips());
        assertEquals(totalBudget, actualTripPlan.getLeftoverBudget());
        assertTrue(actualTripPlan.getNeighborCountries().isEmpty());
        verify(countryService, times(1)).resolveCountryByName(code);
        verify(countryService, never()).resolveCountryByCode(any());
    }

    //TODO: add more tests ...
}
