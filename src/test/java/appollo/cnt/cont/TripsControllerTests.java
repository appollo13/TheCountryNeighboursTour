package appollo.cnt.cont;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import appollo.cnt.model.TripPlanResponse;
import appollo.cnt.service.TripService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class TripsControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripService tripService;

    @Test
    void givenAllIsGood_whenPostToTripsPlan_thenOK() throws Exception {
        // given
        String startingCountry = "BG";
        int budgetPerCountry = 100;
        int totalBudget = 1200;
        String inputCurrency = "BGN";
        TripPlanResponse expected = TripPlanResponse.builder().build();
        when(tripService.planATrip(startingCountry, budgetPerCountry, totalBudget, inputCurrency)).thenReturn(expected);

        // when
        MockHttpServletRequestBuilder requestBuilder = post("/trips/plan")
            .queryParam("startingCountry", startingCountry)
            .queryParam("budgetPerCountry", "" + budgetPerCountry)
            .queryParam("totalBudget", "" + totalBudget)
            .queryParam("inputCurrency", inputCurrency);
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // then
        MvcResult mvcResult = resultActions.andDo(print()).andExpect(status().isOk()).andReturn();
        String jsonString = mvcResult.getResponse().getContentAsString();
        TripPlanResponse actual = objectMapper.readValue(jsonString, TripPlanResponse.class);
        assertEquals(expected, actual);
        verify(tripService, times(1)).planATrip(startingCountry, budgetPerCountry, totalBudget, inputCurrency);
    }

    @Test
    void givenNotExistingInputCurrency_whenPostToTripsPlan_thenOK() throws Exception {
        // given
        String startingCountry = "BG";
        int budgetPerCountry = 100;
        int totalBudget = 1200;
        String inputCurrency = "XYZ";

        // when
        MockHttpServletRequestBuilder requestBuilder = post("/trips/plan")
            .queryParam("startingCountry", startingCountry)
            .queryParam("budgetPerCountry", "" + budgetPerCountry)
            .queryParam("totalBudget", "" + totalBudget)
            .queryParam("inputCurrency", inputCurrency);
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // then
        MvcResult mvcResult = resultActions.andDo(print()).andExpect(status().isBadRequest()).andReturn();
        String jsonString = mvcResult.getResponse().getContentAsString();
        Map<String, Object> actual = objectMapper.readValue(jsonString, Map.class);
        assertEquals("planATrip.inputCurrency: Not a valid ISO-4217 Currency Code", actual.get("message"));
        verify(tripService, never()).planATrip(startingCountry, budgetPerCountry, totalBudget, inputCurrency);
    }

    //TODO: add more tests ...
}
