package appollo.cnt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CountryResponse extends CountryNameAndCodes {

    private List<String> borders;
    private List<Currency> currencies;

    @JsonIgnore
    public CountryNameAndCodes getCountryNameAndCodesOnly() {
        CountryNameAndCodes countryNameAndCodesOnly = new CountryNameAndCodes();
        countryNameAndCodesOnly.setName(getName());
        countryNameAndCodesOnly.setAlpha2Code(getAlpha2Code());
        countryNameAndCodesOnly.setAlpha3Code(getAlpha3Code());
        return countryNameAndCodesOnly;
    }

    @Data
    public static class Currency {

        private String code;
    }
}
