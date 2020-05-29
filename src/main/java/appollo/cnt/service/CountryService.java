package appollo.cnt.service;

import appollo.cnt.client.CountriesClient;
import appollo.cnt.model.CountryResponse;
import feign.FeignException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
        try {
            if (name.length() == 2 || name.length() == 3) {
                CountryResponse country = countriesClient.getCountryByCode(name);
                if (country != null) {
                    return country;
                }
            }

            List<CountryResponse> countriesByName = countriesClient.getCountryByName(name);

            if (countriesByName == null || countriesByName.isEmpty()) {
                throw new NullPointerException("0 countries for '" + name + "' are found!");
            }
            if (countriesByName.size() > 1) {
                throw new IllegalArgumentException(countriesByName.size() + " countries for '" + name + "' are found!");
            }
            return countriesByName.get(0);
        } catch (FeignException.NotFound fe) {
            throw new NullPointerException("0 countries for '" + name + "' are found!");
        }
    }

    @Cacheable("countries")
    public CountryResponse resolveCountryByCode(String code) {
        try {
            CountryResponse country = countriesClient.getCountryByCode(code);
            if (country == null) {
                throw new NullPointerException("0 countries for '" + code + "' are found!");
            }
            return country;
        } catch (FeignException.NotFound fe) {
            throw new NullPointerException("0 countries for '" + code + "' are found!");
        }
    }
}
