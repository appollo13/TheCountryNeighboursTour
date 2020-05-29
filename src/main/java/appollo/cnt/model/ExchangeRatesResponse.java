package appollo.cnt.model;

import java.util.Map;
import lombok.Data;

@Data
public class ExchangeRatesResponse {

    private String base;
    private Map<String, Double> rates;
}
