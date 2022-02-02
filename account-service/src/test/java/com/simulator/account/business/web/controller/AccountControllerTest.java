package com.simulator.account.business.web.controller;

import com.simulator.account.Application;
import com.simulator.account.business.persistence.entity.AccountEntity;
import com.simulator.account.business.service.AccountService;
import com.simulator.account.business.web.helper.AccountHelper;
import com.simulator.account.infrastructure.config.ModelMapperConfig;
import com.simulator.dto.TransactionRequest;
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

import static com.simulator.account.business.web.controller.utils.AccountControllerUtils.getTransactionRequestJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({AccountController.class})
@ContextConfiguration(classes = {Application.class, ModelMapperConfig.class, AccountHelper.class})
public class AccountControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AccountService accountServiceMock;

  @Test
  public void withdraw_shouldWithdrawFunds() throws Exception {
    final var accountNumber = "234566";
    final var pin = "12345";
    final var requestJson = getTransactionRequestJson();

    mockMvc
        .perform(
            put("/v1/accounts/{accountNumber}/withdraw", accountNumber)
                .header("pin", pin)
                .contentType(APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().is(200));

    verify(accountServiceMock, times(1))
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));
  }

  @Test
  public void withdraw_shouldBadRequestWhenPinIsMissing() throws Exception {

    final var accountNumber = "234566";
    final var requestJson = getTransactionRequestJson();

    mockMvc
        .perform(
            put("/v1/accounts/{accountNumber}/withdraw", accountNumber)
                .contentType(APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().is(400))
        .andExpect(jsonPath("status").value("Bad Request"))
        .andExpect(
            jsonPath("description")
                .value(
                    "Required request header 'pin' for method parameter type String is not present"));

    verify(accountServiceMock, times(0))
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));
  }

  @Test
  public void withdraw_shouldDataNotFoundWhenAccountIsNotFound() throws Exception {

    final var accountNumber = "234566";
    final var requestJson = getTransactionRequestJson();
    final var pin = "12345";

    doThrow(new DataNotFoundException("Account number '%s' was not found".formatted(accountNumber)))
        .when(accountServiceMock)
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));

    mockMvc
        .perform(
            put("/v1/accounts/{accountNumber}/withdraw", accountNumber)
                .contentType(APPLICATION_JSON)
                .header("pin", pin)
                .content(requestJson))
        .andExpect(status().is(404))
        .andExpect(jsonPath("status").value("Not Found"))
        .andExpect(
            jsonPath("description")
                .value("Account number '%s' was not found".formatted(accountNumber)));
  }

  @Test
  public void withdraw_shouldBadRequestWhenPinIsInvalid() throws Exception {

    final var accountNumber = "234566";
    final var requestJson = getTransactionRequestJson();
    final var pin = "xxxxx";

    doThrow(new BadRequestException("Pin account is invalid!"))
        .when(accountServiceMock)
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));

    mockMvc
        .perform(
            put("/v1/accounts/{accountNumber}/withdraw", accountNumber)
                .contentType(APPLICATION_JSON)
                .header("pin", pin)
                .content(requestJson))
        .andExpect(status().is(400))
        .andExpect(jsonPath("status").value("Bad Request"))
        .andExpect(jsonPath("description").value("Pin account is invalid!"));
  }

  @Test
  public void withdraw_shouldBadRequestWhenAccountHasInsufficientFunds() throws Exception {

    final var accountNumber = "234566";
    final var requestJson = getTransactionRequestJson();
    final var pin = "1234";

    doThrow(new BadRequestException("Your Account has insufficient funds to complete this request"))
        .when(accountServiceMock)
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));

    mockMvc
        .perform(
            put("/v1/accounts/{accountNumber}/withdraw", accountNumber)
                .contentType(APPLICATION_JSON)
                .header("pin", pin)
                .content(requestJson))
        .andExpect(status().is(400))
        .andExpect(jsonPath("status").value("Bad Request"))
        .andExpect(
            jsonPath("description")
                .value("Your Account has insufficient funds to complete this request"));
  }

  @Test
  public void deposit_shouldDepositFunds() throws Exception {
    final var accountNumber = "234566";
    final var pin = "12345";
    final var requestJson = getTransactionRequestJson();

    mockMvc
        .perform(
            put("/v1/accounts/{accountNumber}/deposit", accountNumber)
                .header("pin", pin)
                .contentType(APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().is(200));

    verify(accountServiceMock, times(1))
        .deposit(anyString(), anyString(), any(TransactionRequest.class));
  }

  @Test
  public void deposit_shouldBadRequestWhenPinIsMissing() throws Exception {

    final var accountNumber = "234566";
    final var requestJson = getTransactionRequestJson();

    mockMvc
        .perform(
            put("/v1/accounts/{accountNumber}/deposit", accountNumber)
                .contentType(APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().is(400))
        .andExpect(jsonPath("status").value("Bad Request"))
        .andExpect(
            jsonPath("description")
                .value(
                    "Required request header 'pin' for method parameter type String is not present"));

    verify(accountServiceMock, times(0))
        .deposit(anyString(), anyString(), any(TransactionRequest.class));
  }

  @Test
  public void deposit_shouldDataNotFoundWhenAccountIsNotFound() throws Exception {

    final var accountNumber = "234566";
    final var requestJson = getTransactionRequestJson();
    final var pin = "12345";

    doThrow(new DataNotFoundException("Account number '%s' was not found".formatted(accountNumber)))
        .when(accountServiceMock)
        .deposit(anyString(), anyString(), any(TransactionRequest.class));

    mockMvc
        .perform(
            put("/v1/accounts/{accountNumber}/deposit", accountNumber)
                .contentType(APPLICATION_JSON)
                .header("pin", pin)
                .content(requestJson))
        .andExpect(status().is(404))
        .andExpect(jsonPath("status").value("Not Found"))
        .andExpect(
            jsonPath("description")
                .value("Account number '%s' was not found".formatted(accountNumber)));
  }

  @Test
  public void deposit_shouldBadRequestWhenPinIsInvalid() throws Exception {

    final var accountNumber = "234566";
    final var requestJson = getTransactionRequestJson();
    final var pin = "xxxxx";

    doThrow(new BadRequestException("Pin account is invalid!"))
        .when(accountServiceMock)
        .deposit(anyString(), anyString(), any(TransactionRequest.class));

    mockMvc
        .perform(
            put("/v1/accounts/{accountNumber}/deposit", accountNumber)
                .contentType(APPLICATION_JSON)
                .header("pin", pin)
                .content(requestJson))
        .andExpect(status().is(400))
        .andExpect(jsonPath("status").value("Bad Request"))
        .andExpect(jsonPath("description").value("Pin account is invalid!"));
  }

  @Test
  public void balance_shouldBalance() throws Exception {
    final var accountNumber = "234566";
    final var pin = "12345";
    final var account =
        AccountEntity.builder()
            .id(1L)
            .pin(pin)
            .accountNumber(accountNumber)
            .balance(BigDecimal.valueOf(800))
            .overdraft(BigDecimal.valueOf(200))
            .build();

    when(accountServiceMock.balance(anyString(), anyString())).thenReturn(account);

    mockMvc
        .perform(
            get("/v1/accounts/{accountNumber}/balance", accountNumber)
                .header("pin", pin)
                .contentType(APPLICATION_JSON))
        .andExpect(status().is(200));

    verify(accountServiceMock, times(1)).balance(anyString(), anyString());
  }

  @Test
  public void balance_shouldBadRequestWhenPinIsMissing() throws Exception {

    final var accountNumber = "234566";

    mockMvc
        .perform(
            get("/v1/accounts/{accountNumber}/balance", accountNumber)
                .contentType(APPLICATION_JSON))
        .andExpect(status().is(400))
        .andExpect(jsonPath("status").value("Bad Request"))
        .andExpect(
            jsonPath("description")
                .value(
                    "Required request header 'pin' for method parameter type String is not present"));

    verify(accountServiceMock, times(0)).balance(anyString(), anyString());
  }

  @Test
  public void balance_shouldDataNotFoundWhenAccountIsNotFound() throws Exception {

    final var accountNumber = "234566";
    final var pin = "12345";

    doThrow(new DataNotFoundException("Account number '%s' was not found".formatted(accountNumber)))
        .when(accountServiceMock)
        .balance(anyString(), anyString());

    mockMvc
        .perform(
            get("/v1/accounts/{accountNumber}/balance", accountNumber)
                .header("pin", pin)
                .contentType(APPLICATION_JSON))
        .andExpect(status().is(404))
        .andExpect(jsonPath("status").value("Not Found"))
        .andExpect(
            jsonPath("description")
                .value("Account number '%s' was not found".formatted(accountNumber)));

    verify(accountServiceMock, times(1)).balance(anyString(), anyString());
  }

  @Test
  public void balance_shouldBadRequestWhenPinIsInvalid() throws Exception {

    final var accountNumber = "234566";
    final var pin = "xxxxx";

    doThrow(new BadRequestException("Pin account is invalid!"))
        .when(accountServiceMock)
        .balance(anyString(), anyString());

    mockMvc
        .perform(
            get("/v1/accounts/{accountNumber}/balance", accountNumber)
                .header("pin", pin)
                .contentType(APPLICATION_JSON))
        .andExpect(status().is(400))
        .andExpect(jsonPath("status").value("Bad Request"))
        .andExpect(jsonPath("description").value("Pin account is invalid!"));
  }
}
