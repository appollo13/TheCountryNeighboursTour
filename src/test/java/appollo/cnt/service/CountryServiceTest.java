package appollo.cnt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import appollo.cnt.client.CountriesClient;
import appollo.cnt.model.CountryNameAndCodes;
import appollo.cnt.model.CountryResponse;
import feign.FeignException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {

    @Mock
    private CountriesClient countriesClient;

    @Spy
    @InjectMocks
    private CountryService countryService;

    private CountryResponse getCountryResponse() {
        CountryResponse expected = new CountryResponse();

        CountryNameAndCodes names = new CountryNameAndCodes();
        names.setName("Third Bulgarian State 1878–present");
        expected.setCountry(names);

        return expected;
    }

    @Test
    public void givenExistingCode_whenResolveCountryByName_thenCountryResponseFromCode() {
        // given
        String code = "BG";
        CountryResponse expected = getCountryResponse();
        when(countriesClient.getCountryByCode(code)).thenReturn(expected);

        // when
        CountryResponse actual = countryService.resolveCountryByName(code);

        // then
        assertEquals(expected, actual);
        verify(countriesClient, times(1)).getCountryByCode(code);
        verify(countriesClient, never()).getCountryByName(any());
    }

    @Test
    public void givenNotExistingCodeButExistingAsName_whenResolveCountryByName_thenCountryResponseFromName() {
        // given
        String code = "BUL";
        CountryResponse expected = getCountryResponse();
        when(countriesClient.getCountryByCode(code)).thenReturn(null);
        when(countriesClient.getCountryByName(code)).thenReturn(Collections.singletonList(expected));

        // when
        CountryResponse actual = countryService.resolveCountryByName(code);

        // then
        assertEquals(expected, actual);
        verify(countriesClient, times(1)).getCountryByCode(code);
        verify(countriesClient, times(1)).getCountryByName(code);
    }

    @Test
    public void givenExistingName_whenResolveCountryByName_thenCountryResponse() {
        // given
        String name = "Bulgaria";
        CountryResponse expected = getCountryResponse();
        when(countriesClient.getCountryByName(name)).thenReturn(Collections.singletonList(expected));

        // when
        CountryResponse actual = countryService.resolveCountryByName(name);

        // then
        assertEquals(expected, actual);
        verify(countriesClient, never()).getCountryByCode(any());
        verify(countriesClient, times(1)).getCountryByName(name);
    }

    @Test
    public void givenNotExistingName_whenResolveCountryByName_thenNPE() {
        // given
        String name = "deVRealm";
        when(countriesClient.getCountryByName(name)).thenReturn(null);

        // when
        assertThrows(NullPointerException.class, () -> countryService.resolveCountryByName(name));

        // then
        verify(countriesClient, times(1)).getCountryByName(name);
    }

    @Test
    public void givenAmbiguousName_whenResolveCountryByName_thenIllegalArgumentException() {
        // given
        String name = "people";
        List<CountryResponse> countriesByName = Arrays
            .asList(mock(CountryResponse.class), mock(CountryResponse.class));
        when(countriesClient.getCountryByName(name)).thenReturn(countriesByName);

        // when
        assertThrows(IllegalArgumentException.class, () -> countryService.resolveCountryByName(name));

        // then
        verify(countriesClient, times(1)).getCountryByName(name);
    }

    @Test
    public void givenFeignExceptionNotFound_whenResolveCountryByName_thenNPE() {
        // given
        String name = "deVRealm";
        when(countriesClient.getCountryByName(name)).thenThrow(FeignException.NotFound.class);

        // when
        assertThrows(NullPointerException.class, () -> countryService.resolveCountryByName(name));

        // then
        verify(countriesClient, times(1)).getCountryByName(name);
    }

    @Test
    public void givenExistingCode_whenResolveCountryByCode_thenCountryResponse() {
        // given
        String code = "BG";
        CountryResponse expected = getCountryResponse();
        when(countriesClient.getCountryByCode(code)).thenReturn(expected);

        // when
        CountryResponse actual = countryService.resolveCountryByCode(code);

        // then
        assertEquals(expected, actual);
        verify(countriesClient, times(1)).getCountryByCode(code);
    }

    @Test
    public void givenNotExistingCode_whenResolveCountryByCode_thenNPE() {
        // given
        String code = "XYZ";
        when(countriesClient.getCountryByCode(code)).thenReturn(null);

        // when
        assertThrows(NullPointerException.class, () -> countryService.resolveCountryByCode(code));

        // then
        verify(countriesClient, times(1)).getCountryByCode(code);
    }

    @Test
    public void givenFeignExceptionNotFound_whenResolveCountryByCode_thenNPE() {
        // given
        String code = "XYZ";
        when(countriesClient.getCountryByCode(code)).thenThrow(FeignException.NotFound.class);

        // when
        assertThrows(NullPointerException.class, () -> countryService.resolveCountryByCode(code));

        // then
        verify(countriesClient, times(1)).getCountryByCode(code);
    }
}
