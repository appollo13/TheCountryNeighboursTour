package appollo.cnt.client;

import appollo.cnt.model.CountryResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${feign.client.countries.name}", url = "${feign.client.countries.url}")
public interface CountriesClient {

    @GetMapping(value = "/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<CountryResponse> getCountryByName(@PathVariable("name") String name);

    @GetMapping(value = "/alpha/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    CountryResponse getCountryByCode(@PathVariable("code") String name);
}
