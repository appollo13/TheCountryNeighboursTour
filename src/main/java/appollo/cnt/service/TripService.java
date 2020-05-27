package appollo.cnt.service;

import org.springframework.stereotype.Service;

@Service
public class TripService {

    public String planATrip(String startingCountry, int budgetPerCountry, int totalBudget, String inputCurrency) {
        /* TODO the plan is as follows:
        1. get the country and its neighbours - https://restcountries.eu/rest/v2/name/{name}
        //2. get the currencies in each country
        //3. get the needed exchange rates
        4. do the calculations
        5. create the result/response
         */
        return startingCountry + ", " + budgetPerCountry + ", " + totalBudget + ", " + inputCurrency;
    }
}
