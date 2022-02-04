package com.simulator.atm;

import com.simulator.atm.business.service.AtmService;
import com.simulator.exception.BadRequestException;
import com.simulator.exception.DataNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.web.servlet.MockMvc;

import static com.simulator.atm.business.web.controller.utils.AtmControllerUtils.getRetryableException;
import static com.simulator.atm.business.web.controller.utils.AtmControllerUtils.getTransactionRequestJson;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

        final var accountNumber = "123456789";

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
    public void Integration_balance_shouldDataNotFoundWhenAccountIsNotFound() throws Exception {

        final var accountNumber = "54644";
        final var pin = "1234";

        mockMvc
            .perform(
                get("/v1/atm/balance")
                    .header("accountNumber", accountNumber)
                    .header("pin", pin)
                    .contentType(APPLICATION_JSON))
            .andExpect(status().is(404))
            .andExpect(jsonPath("status").value("Not Found"))
            .andExpect(
                jsonPath("description")
                    .value("Account number '%s' was not found".formatted(accountNumber)));

    }

    @Test
    public void balance_shouldBadRequestWhenPinIsInvalid() throws Exception {

        final var accountNumber = "123456789";
        final var pin = "xxxx";

        mockMvc
            .perform(
                get("/v1/atm/balance")
                    .header("accountNumber", accountNumber)
                    .header("pin", pin)
                    .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(jsonPath("description").value("Pin account is invalid!"));

    }

    @Test
    public void Integration_dispense_shouldDispenseMoney() throws Exception {
        final var accountNumber = "123456789";
        final var pin = "1234";
        final var requestJson = getTransactionRequestJson();

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
    public void Integration_dispense_shouldDataNotFoundWhenAccountIsNotFound() throws Exception {

        final var accountNumber = "54644";
        final var pin = "1234";
        final var requestJson = getTransactionRequestJson();

        mockMvc
            .perform(
                post("/v1/atm/dispense")
                    .header("accountNumber", accountNumber)
                    .header("pin", pin)
                    .content(requestJson)
                    .contentType(APPLICATION_JSON))
            .andExpect(status().is(404))
            .andExpect(jsonPath("status").value("Not Found"))
            .andExpect(
                jsonPath("description")
                    .value("Account number '%s' was not found".formatted(accountNumber)));

    }

    @Test
    public void Integration_dispense_shouldBadRequestWhenAccountHasInsufficientFunds() throws Exception {

        final var accountNumber = "123456789";
        final var pin = "1234";
        final var requestJson = getTransactionRequestJson();

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
                    .value("Your Account has insufficient funds to complete this request"));

    }
}
