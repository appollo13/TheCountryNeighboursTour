package appollo.cnt.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import lombok.Data;

@Data
public class CountryResponse {

    @JsonUnwrapped
    private CountryNameAndCodes country;
    private List<String> borders;
    private List<Currency> currencies;

    @Data
    public static class Currency {

        private String code;
    }
}
