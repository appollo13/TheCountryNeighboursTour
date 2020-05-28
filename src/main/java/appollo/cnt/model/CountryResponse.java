package appollo.cnt.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CountryResponse extends CountryNameAndCodes {

    private List<String> borders;
    private List<Currency> currencies;

    @Data
    public static class Currency {

        private String code;
    }
}
