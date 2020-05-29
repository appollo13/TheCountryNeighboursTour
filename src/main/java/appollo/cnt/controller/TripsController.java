package appollo.cnt.controller;

import appollo.cnt.model.TripPlanResponse;
import appollo.cnt.service.TripService;
import appollo.cnt.validation.Iso4217CurrencyCodeConstraint;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("trips")
@Validated
public class TripsController {

    private final TripService tripService;

    @Autowired
    public TripsController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping(value = "/plan", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TripPlanResponse planATrip(
        @RequestParam @Valid @NotBlank String startingCountry,
        @RequestParam @Valid @Min(1) int budgetPerCountry,
        @RequestParam @Valid @Min(1) int totalBudget,
        @RequestParam(required = false) @Valid @Iso4217CurrencyCodeConstraint(required = false) String inputCurrency
    ) {
        return tripService.planATrip(startingCountry, budgetPerCountry, totalBudget, inputCurrency);
    }
}
