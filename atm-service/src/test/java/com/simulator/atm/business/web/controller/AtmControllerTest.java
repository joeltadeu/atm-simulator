package com.simulator.atm.business.web.controller;

import com.simulator.atm.Application;
import com.simulator.atm.business.service.AtmService;
import com.simulator.atm.business.web.helper.AtmHelper;
import com.simulator.atm.infrastructure.config.ModelMapperConfig;
import com.simulator.dto.AccountBalanceDto;
import com.simulator.exception.BadRequestException;
import com.simulator.exception.DataNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.simulator.atm.business.web.controller.utils.AtmControllerUtils.getRetryableException;
import static com.simulator.atm.business.web.controller.utils.AtmControllerUtils.getTransactionRequestJson;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({ AtmController.class})
@ContextConfiguration(classes = { Application.class, ModelMapperConfig.class, AtmHelper.class })
public class AtmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtmService atmServiceMock;

    @Test
    public void balance_shouldBalance() throws Exception {
        final var accountNumber = "234566";
        final var pin = "12345";
        final var account = new AccountBalanceDto(BigDecimal.valueOf(800), BigDecimal.valueOf(200));

        when(atmServiceMock.balance(anyString(), anyString())).thenReturn(account);

        mockMvc.perform(get("/v1/atm/balance")
                .header("accountNumber", accountNumber)
                .header("pin", pin)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(200));

        verify(atmServiceMock, times(1)).balance(anyString(), anyString());
    }

    @Test
    public void balance_shouldBadRequestWhenPinIsMissing() throws Exception {

        final var accountNumber = "234566";

        mockMvc.perform(get("/v1/atm/balance")
                .header("accountNumber", accountNumber)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(jsonPath("description").value("Required request header 'pin' for method parameter type String is not present"));

        verify(atmServiceMock, times(0)).balance(anyString(), anyString());
    }

    @Test
    public void balance_shouldBadRequestWhenAccountNumberIsMissing() throws Exception {

        final var pin = "12345";

        mockMvc.perform(get("/v1/atm/balance")
                .header("pin", pin)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(jsonPath("description").value("Required request header 'accountNumber' for method parameter type String is not present"));

        verify(atmServiceMock, times(0)).balance(anyString(), anyString());
    }

    @Test
    public void balance_shouldDataNotFoundWhenAccountIsNotFound() throws Exception {

        final var accountNumber = "234566";
        final var pin = "12345";

        doThrow(new DataNotFoundException("Account number '%s' was not found".formatted(accountNumber)))
            .when(atmServiceMock).balance(anyString(), anyString());

        mockMvc.perform(get("/v1/atm/balance")
                .header("accountNumber", accountNumber)
                .header("pin", pin)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(404))
            .andExpect(jsonPath("status").value("Not Found"))
            .andExpect(jsonPath("description").value("Account number '%s' was not found".formatted(accountNumber)));

        verify(atmServiceMock, times(1)).balance(anyString(), anyString());
    }

    @Test
    public void balance_shouldBadRequestWhenPinIsInvalid() throws Exception {

        final var accountNumber = "234566";
        final var pin = "xxxxx";

        doThrow(new BadRequestException("Pin account is invalid!"))
            .when(atmServiceMock).balance(anyString(), anyString());

        mockMvc.perform(get("/v1/atm/balance")
                .header("accountNumber", accountNumber)
                .header("pin", pin)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(jsonPath("description").value("Pin account is invalid!"));

        verify(atmServiceMock, times(1)).balance(anyString(), anyString());
    }

    @Test
    public void balance_shouldInternalServerErrorWhenAccountServiceIsDown() throws Exception {

        final var accountNumber = "234566";
        final var pin = "xxxxx";

        doThrow(getRetryableException()).when(atmServiceMock).balance(anyString(), anyString());

        mockMvc.perform(get("/v1/atm/balance")
                .header("accountNumber", accountNumber)
                .header("pin", pin)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(500))
            .andExpect(jsonPath("status").value("Internal Server Error"));

        verify(atmServiceMock, times(1)).balance(anyString(), anyString());
    }

    @Test
    public void dispense_shouldDispenseMoney() throws Exception {
        final var accountNumber = "234566";
        final var pin = "12345";
        final var requestJson = getTransactionRequestJson();

        mockMvc.perform(post("/v1/atm/dispense")
                .header("accountNumber", accountNumber)
                .header("pin", pin)
                .contentType(APPLICATION_JSON)
                .content( requestJson))
            .andExpect(status().is(200));

        verify(atmServiceMock, times(1)).dispense(anyString(), anyString(), anyInt());
    }

    @Test
    public void dispense_shouldBadRequestWhenPinIsMissing() throws Exception {

        final var accountNumber = "234566";

        mockMvc.perform(post("/v1/atm/dispense")
                .header("accountNumber", accountNumber)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(jsonPath("description").value("Required request header 'pin' for method parameter type String is not present"));

        verify(atmServiceMock, times(0)).dispense(anyString(), anyString(), anyInt());
    }

    @Test
    public void dispense_shouldBadRequestWhenAccountNumberIsMissing() throws Exception {

        final var pin = "12345";

        mockMvc.perform(post("/v1/atm/dispense")
                .header("pin", pin)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(jsonPath("description").value("Required request header 'accountNumber' for method parameter type String is not present"));

        verify(atmServiceMock, times(0)).dispense(anyString(), anyString(), anyInt());
    }

    @Test
    public void dispense_shouldDataNotFoundWhenAccountIsNotFound() throws Exception {

        final var accountNumber = "234566";
        final var pin = "12345";
        final var requestJson = getTransactionRequestJson();

        doThrow(new DataNotFoundException("Account number '%s' was not found".formatted(accountNumber)))
            .when(atmServiceMock).dispense(anyString(), anyString(), anyInt());

        mockMvc.perform(post("/v1/atm/dispense")
                .header("accountNumber", accountNumber)
                .header("pin", pin)
                .content( requestJson)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(404))
            .andExpect(jsonPath("status").value("Not Found"))
            .andExpect(jsonPath("description").value("Account number '%s' was not found".formatted(accountNumber)));

        verify(atmServiceMock, times(1)).dispense(anyString(), anyString(), anyInt());
    }

    @Test
    public void dispense_shouldBadRequestWhenAccountHasInsufficientFunds() throws Exception {

        final var accountNumber = "234566";
        final var pin = "12345";
        final var requestJson = getTransactionRequestJson();

        doThrow(new BadRequestException("Your Account has insufficient funds to complete this request"))
            .when(atmServiceMock).dispense(anyString(), anyString(), anyInt());

        mockMvc.perform(post("/v1/atm/dispense")
                .header("accountNumber", accountNumber)
                .header("pin", pin)
                .content( requestJson)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(jsonPath("description").value("Your Account has insufficient funds to complete this request"));

        verify(atmServiceMock, times(1)).dispense(anyString(), anyString(), anyInt());
    }

    @Test
    public void dispense_shouldBadRequestWhenAtmHasInsufficientFunds() throws Exception {

        final var accountNumber = "234566";
        final var pin = "12345";
        final var requestJson = getTransactionRequestJson();

        doThrow(new BadRequestException("Atm does not have the funds to complete your request"))
            .when(atmServiceMock).dispense(anyString(), anyString(), anyInt());

        mockMvc.perform(post("/v1/atm/dispense")
                .header("accountNumber", accountNumber)
                .header("pin", pin)
                .content( requestJson)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(jsonPath("description").value("Atm does not have the funds to complete your request"));

        verify(atmServiceMock, times(1)).dispense(anyString(), anyString(), anyInt());
    }

    @Test
    public void dispense_shouldBadRequestWhenAtmCannotDispenseRequestedAmount() throws Exception {

        final var accountNumber = "234566";
        final var pin = "12345";
        final var requestJson = getTransactionRequestJson();

        doThrow(new BadRequestException("It is not possible to dispense this value"))
            .when(atmServiceMock).dispense(anyString(), anyString(), anyInt());

        mockMvc.perform(post("/v1/atm/dispense")
                .header("accountNumber", accountNumber)
                .header("pin", pin)
                .content( requestJson)
                .contentType(APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("status").value("Bad Request"))
            .andExpect(jsonPath("description").value("It is not possible to dispense this value"));

        verify(atmServiceMock, times(1)).dispense(anyString(), anyString(), anyInt());
    }
}
