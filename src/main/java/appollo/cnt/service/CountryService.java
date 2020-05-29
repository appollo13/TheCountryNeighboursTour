package appollo.cnt.service;

import appollo.cnt.client.CountriesClient;
import appollo.cnt.model.CountryResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CountryService {

    private final CountriesClient countriesClient;

    @Autowired
    public CountryService(CountriesClient countriesClient) {
        this.countriesClient = countriesClient;
    }

    @Cacheable("countries")
    public CountryResponse resolveCountryByName(String name) {
        if (name.length() == 2 || name.length() == 3) {
            CountryResponse country = countriesClient.getCountryByCode(name);
            if (country != null) {
                return country;
            }
        }

        List<CountryResponse> countriesByName = countriesClient.getCountryByName(name);
        if (countriesByName == null || countriesByName.isEmpty()) {
            throw new RuntimeException(HttpStatus.NOT_FOUND.toString());
        }
        if (countriesByName.size() > 1) {
            throw new RuntimeException(HttpStatus.CONFLICT.toString());
        }
        return countriesByName.get(0);
    }

    @Cacheable("countries")
    public CountryResponse resolveCountryByCode(String code) {
        CountryResponse country = countriesClient.getCountryByCode(code);
        if (country == null) {
            throw new RuntimeException(HttpStatus.NOT_FOUND.toString());
        }
        return country;
    }
}
