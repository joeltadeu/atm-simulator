package com.simulator.atm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.web.servlet.MockMvc;

import static com.simulator.atm.business.web.controller.utils.AtmControllerUtils.getTransactionRequestJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureStubRunner(ids = {"com.simulator:account-service:+:stubs:7501"}, stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class AtmIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void Integration_balance_shouldBalance() throws Exception {
        final var accountNumber = "123456789";
        final var pin = "1234";

        mockMvc
            .perform(
                get("/v1/atm/balance")
                    .header("accountNumber", accountNumber)
                    .header("pin", pin)
                    .contentType(APPLICATION_JSON))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.balance").isNumber())
            .andExpect(jsonPath("$.overdraft").isNumber());

    }

    @Test
    public void Integration_balance_shouldBadRequestWhenPinIsMissing() throws Exception {

        final var accountNumber = "987654321";

        mockMvc
            .perform(
                get("/v1/atm/balance")
                    .header("accountNumber", accountNumber)
                    .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(
                jsonPath("description")
                    .value(
                        "Required request header 'pin' for method parameter type String is not present"));

    }

    @Test
    public void Integration_balance_shouldBadRequestWhenAccountNumberIsMissing() throws Exception {

        final var pin = "1234";

        mockMvc
            .perform(get("/v1/atm/balance").header("pin", pin).contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(
                jsonPath("description")
                    .value(
                        "Required request header 'accountNumber' for method parameter type String is not present"));

    }

    @Test
    public void Integration_dispense_shouldDispenseMoney() throws Exception {
        final var accountNumber = "123456789";
        final var pin = "1234";
        final var requestJson = getTransactionRequestJson(500);

        mockMvc
            .perform(
                post("/v1/atm/dispense")
                    .header("accountNumber", accountNumber)
                    .header("pin", pin)
                    .contentType(APPLICATION_JSON)
                    .content(requestJson))
            .andExpect(status().is(200));

    }

    @Test
    public void Integration_dispense_shouldBadRequestWhenPinIsMissing() throws Exception {

        final var accountNumber = "123456789";

        mockMvc
            .perform(
                post("/v1/atm/dispense")
                    .header("accountNumber", accountNumber)
                    .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(
                jsonPath("description")
                    .value(
                        "Required request header 'pin' for method parameter type String is not present"));
    }

    @Test
    public void Integration_dispense_shouldBadRequestWhenAccountNumberIsMissing() throws Exception {

        final var pin = "1234";

        mockMvc
            .perform(post("/v1/atm/dispense").header("pin", pin).contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(
                jsonPath("description")
                    .value(
                        "Required request header 'accountNumber' for method parameter type String is not present"));

    }

    @Test
    public void Integration_dispense_shouldBadRequestWhenAtmDoesNotHaveFunds() throws Exception {

        final var accountNumber = "123456789";
        final var pin = "1234";
        final var requestJson = getTransactionRequestJson(2000);

        mockMvc
            .perform(
                post("/v1/atm/dispense")
                    .header("accountNumber", accountNumber)
                    .header("pin", pin)
                    .content(requestJson)
                    .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(
                jsonPath("description")
                    .value("Atm does not have the funds to complete your request"));

    }
}
