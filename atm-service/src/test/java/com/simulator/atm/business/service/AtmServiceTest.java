package com.simulator.atm.business.service;

import com.simulator.atm.business.service.client.AccountServiceClient;
import com.simulator.atm.business.service.dispenser.CashDispenser;
import com.simulator.dto.AccountBalanceDto;
import com.simulator.dto.TransactionRequest;
import com.simulator.exception.BadRequestException;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.simulator.atm.business.service.utils.AtmServiceUtils.getBadRequestExceptionAtmCanNotGiveAmount;
import static com.simulator.atm.business.service.utils.AtmServiceUtils.getBadRequestExceptionAtmHasNoCash;
import static com.simulator.atm.business.service.utils.AtmServiceUtils.getCashDispensed;
import static com.simulator.atm.business.service.utils.AtmServiceUtils.getRetryableExceptionAccountHasInsufficientFunds;
import static com.simulator.atm.business.service.utils.AtmServiceUtils.getRetryableExceptionAccountIsNotFound;
import static com.simulator.atm.business.service.utils.AtmServiceUtils.getRetryableExceptionPinIsInvalid;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AtmServiceTest {

  @InjectMocks private AtmService atmService;

  @Mock private AccountServiceClient accountClient;

  @Mock private CashDispenser cashDispenser;

  @Test
  public void balance_shouldGetBalance() {

    final var accountNumber = "234566";
    final var pin = "12345";

    final var account = new AccountBalanceDto(BigDecimal.valueOf(800), BigDecimal.valueOf(200));

    when(accountClient.balance(accountNumber, pin)).thenReturn(account);

    final var returnedAccount = atmService.balance(accountNumber, pin);

    assertEquals(account, returnedAccount);
    verify(accountClient, times(1)).balance(anyString(), anyString());
  }

  @Test
  public void balance_shouldBadRequestWhenPinIsInvalid() {

    final var accountNumber = "234566";
    final var pin = "12345";

    doThrow(getRetryableExceptionPinIsInvalid())
        .when(accountClient)
        .balance(anyString(), anyString());

    final var assertThrows =
        assertThrows(FeignException.class, () -> atmService.balance(accountNumber, pin));

    assertEquals("Pin account is invalid!", assertThrows.getMessage());
    assertEquals(400, assertThrows.status());

    verify(accountClient, times(1)).balance(anyString(), anyString());
  }

  @Test
  public void balance_shouldDataNotFoundWhenAccountIsNotFound() {

    final var accountNumber = "234566";
    final var pin = "12345";

    doThrow(getRetryableExceptionAccountIsNotFound(accountNumber))
        .when(accountClient)
        .balance(anyString(), anyString());

    final var assertThrows =
        assertThrows(FeignException.class, () -> atmService.balance(accountNumber, pin));

    assertEquals(
        "Account number '%s' was not found".formatted(accountNumber), assertThrows.getMessage());
    assertEquals(404, assertThrows.status());

    verify(accountClient, times(1)).balance(anyString(), anyString());
  }

  @Test
  public void dispense_shouldDispenseFunds() {

    final var accountNumber = "234566";
    final var pin = "12345";
    final var amount = 150;

    final var cashDispensed = getCashDispensed();

    when(cashDispenser.dispense(amount)).thenReturn(cashDispensed);

    final var returnedCashDispensed = atmService.dispense(accountNumber, pin, amount);

    assertEquals(cashDispensed, returnedCashDispensed);
    verify(accountClient, times(1))
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));
  }

  @Test
  public void dispense_shouldBadRequestWhenAtmHasNoCash() {

    final var accountNumber = "234566";
    final var pin = "12345";
    final var amount = 150;

    doThrow(getBadRequestExceptionAtmHasNoCash()).when(cashDispenser).dispense(anyInt());

    final var assertThrows =
        assertThrows(
            BadRequestException.class, () -> atmService.dispense(accountNumber, pin, amount));

    assertEquals("Atm does not have the funds to complete your request", assertThrows.getMessage());
    assertEquals(400, assertThrows.getStatus().value());

    verify(accountClient, times(0))
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));
  }

  @Test
  public void dispense_shouldBadRequestWhenAtmCanNotGiveAmount() {

    final var accountNumber = "234566";
    final var pin = "12345";
    final var amount = 150;

    doThrow(getBadRequestExceptionAtmCanNotGiveAmount()).when(cashDispenser).dispense(anyInt());

    final var assertThrows =
        assertThrows(
            BadRequestException.class, () -> atmService.dispense(accountNumber, pin, amount));

    assertEquals("It is not possible to dispense this value", assertThrows.getMessage());
    assertEquals(400, assertThrows.getStatus().value());

    verify(accountClient, times(0))
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));
  }

  @Test
  public void dispense_shouldBadRequestWhenAccountWasNotFound() {

    final var accountNumber = "234566";
    final var pin = "12345";
    final var amount = 150;

    final var cashDispensed = getCashDispensed();

    when(cashDispenser.dispense(amount)).thenReturn(cashDispensed);

    doThrow(getRetryableExceptionAccountIsNotFound(accountNumber))
        .when(accountClient)
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));

    final var assertThrows =
        assertThrows(FeignException.class, () -> atmService.dispense(accountNumber, pin, amount));

    assertEquals(
        "Account number '%s' was not found".formatted(accountNumber), assertThrows.getMessage());
    assertEquals(404, assertThrows.status());

    verify(accountClient, times(1))
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));
    verify(cashDispenser, times(1)).refund(anyList());
  }

  @Test
  public void dispense_shouldBadRequestWhenPinIsInvalid() {

    final var accountNumber = "234566";
    final var pin = "12345";
    final var amount = 150;

    final var cashDispensed = getCashDispensed();

    when(cashDispenser.dispense(amount)).thenReturn(cashDispensed);

    doThrow(getRetryableExceptionPinIsInvalid())
        .when(accountClient)
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));

    final var assertThrows =
        assertThrows(FeignException.class, () -> atmService.dispense(accountNumber, pin, amount));

    assertEquals("Pin account is invalid!", assertThrows.getMessage());
    assertEquals(400, assertThrows.status());

    verify(accountClient, times(1))
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));
    verify(cashDispenser, times(1)).refund(anyList());
  }

  @Test
  public void dispense_shouldBadRequestWhenAccountHasInsufficientFunds() {

    final var accountNumber = "234566";
    final var pin = "12345";
    final var amount = 150;

    final var cashDispensed = getCashDispensed();

    when(cashDispenser.dispense(amount)).thenReturn(cashDispensed);

    doThrow(getRetryableExceptionAccountHasInsufficientFunds())
        .when(accountClient)
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));

    final var assertThrows =
        assertThrows(FeignException.class, () -> atmService.dispense(accountNumber, pin, amount));

    assertEquals(
        "Your Account has insufficient funds to complete this request", assertThrows.getMessage());
    assertEquals(400, assertThrows.status());

    verify(accountClient, times(1))
        .withdraw(anyString(), anyString(), any(TransactionRequest.class));
    verify(cashDispenser, times(1)).refund(anyList());
  }
}
