package appollo.cnt.client;

import appollo.cnt.model.ExchangeRatesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${feign.client.exchangerates.name}", url = "${feign.client.exchangerates.url}")
public interface ExchangeRatesClient {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ExchangeRatesResponse getCountryByName(@RequestParam("base") String base);
}
