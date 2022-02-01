package com.simulator.account.business.service;

import com.simulator.account.business.persistence.entity.AccountEntity;
import com.simulator.account.business.persistence.repository.AccountRepository;
import com.simulator.exception.BadRequestException;
import com.simulator.exception.DataNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.simulator.account.business.service.utils.AccountServiceUtils.getAccountEntity;
import static com.simulator.account.business.service.utils.AccountServiceUtils.getTransactionRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

  @Mock private AccountRepository accountRepositoryMock;

  @InjectMocks private AccountService accountService;

  @Test
  public void getBalance_shouldGetBalance() {
    final var accountNumber = "234566";
    final var pin = "12345";

    final var returnedAccount = getAccountEntity(accountNumber, pin, 800, 200);

    when(accountRepositoryMock.findByAccountNumber(accountNumber))
        .thenReturn(Optional.of(returnedAccount));

    final var accountReturnedFromDatabase = accountService.balance(accountNumber, pin);

    assertEquals(returnedAccount, accountReturnedFromDatabase);

    verify(accountRepositoryMock, times(1)).findByAccountNumber(anyString());
  }

    @Test
    public void getBalance_shouldDataNotFoundWhenAccountIsNotFound() {
        final var accountNumber = "234566";
        final var pin = "12345";

        when(accountRepositoryMock.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        final var assertThrows =
            assertThrows(DataNotFoundException.class, () -> accountService.balance(accountNumber, pin));

        assertEquals("Account number '%s' was not found".formatted(accountNumber), assertThrows.getMessage());

        verify(accountRepositoryMock, times(1)).findByAccountNumber(anyString());
    }

    @Test
    public void getBalance_shouldBadRequestWhenPinIsInvalid() {
        final var accountNumber = "234566";
        final var pin = "12345";

        final var returnedAccount =
            getAccountEntity(accountNumber, "4321", 800, 200);

        when(accountRepositoryMock.findByAccountNumber(accountNumber))
            .thenReturn(Optional.of(returnedAccount));

        final var assertThrows =
            assertThrows(BadRequestException.class, () -> accountService.balance(accountNumber, pin));

        assertEquals("Pin account is invalid!", assertThrows.getMessage());

        verify(accountRepositoryMock, times(1)).findByAccountNumber(anyString());
    }

    @Test
    public void withdraw_shouldWithdrawFunds() {
        final var accountNumber = "234566";
        final var pin = "12345";

        final var transaction = getTransactionRequest(500);

        final var returnedAccount =
            getAccountEntity(accountNumber, pin, 800, 200);

        when(accountRepositoryMock.findByAccountNumber(accountNumber))
            .thenReturn(Optional.of(returnedAccount));

        accountService.withdraw(accountNumber, pin, transaction);

        verify(accountRepositoryMock, times(1)).findByAccountNumber(anyString());
        verify(accountRepositoryMock, times(1)).save(any(AccountEntity.class));
    }

    @Test
    public void withdraw_shouldDataNotFoundWhenAccountIsNotFound() {
        final var accountNumber = "234566";
        final var pin = "12345";
        final var transaction = getTransactionRequest(500);

        when(accountRepositoryMock.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        final var assertThrows =
            assertThrows(DataNotFoundException.class, () -> accountService.withdraw(accountNumber, pin, transaction));

        assertEquals("Account number '%s' was not found".formatted(accountNumber), assertThrows.getMessage());

        verify(accountRepositoryMock, times(1)).findByAccountNumber(anyString());
        verify(accountRepositoryMock, times(0)).save(any(AccountEntity.class));
    }

    @Test
    public void withdraw_shouldBadRequestWhenPinIsInvalid() {
        final var accountNumber = "234566";
        final var pin = "12345";
        final var transaction = getTransactionRequest(500);

        final var returnedAccount =
            getAccountEntity(accountNumber, "4321", 800, 200);

        when(accountRepositoryMock.findByAccountNumber(accountNumber))
            .thenReturn(Optional.of(returnedAccount));

        final var assertThrows =
            assertThrows(BadRequestException.class, () -> accountService.withdraw(accountNumber, pin, transaction));

        assertEquals("Pin account is invalid!", assertThrows.getMessage());

        verify(accountRepositoryMock, times(1)).findByAccountNumber(anyString());
        verify(accountRepositoryMock, times(0)).save(any(AccountEntity.class));
    }

    @Test
    public void withdraw_shouldBadRequestWhenAccountHasInsufficientFunds() {
        final var accountNumber = "234566";
        final var pin = "12345";
        final var transaction = getTransactionRequest(800);

        final var returnedAccount =
            getAccountEntity(accountNumber, pin, 200, 200);

        when(accountRepositoryMock.findByAccountNumber(accountNumber))
            .thenReturn(Optional.of(returnedAccount));

        final var assertThrows =
            assertThrows(BadRequestException.class, () -> accountService.withdraw(accountNumber, pin, transaction));

        assertEquals("Your Account has insufficient funds to complete this request", assertThrows.getMessage());

        verify(accountRepositoryMock, times(1)).findByAccountNumber(anyString());
        verify(accountRepositoryMock, times(0)).save(any(AccountEntity.class));
    }

    @Test
    public void deposit_shouldDepositFunds() {
        final var accountNumber = "234566";
        final var pin = "12345";

        final var transaction = getTransactionRequest(500);

        final var returnedAccount =
            getAccountEntity(accountNumber, pin, 800, 200);

        when(accountRepositoryMock.findByAccountNumber(accountNumber))
            .thenReturn(Optional.of(returnedAccount));

        accountService.deposit(accountNumber, pin, transaction);

        verify(accountRepositoryMock, times(1)).findByAccountNumber(anyString());
        verify(accountRepositoryMock, times(1)).save(any(AccountEntity.class));
    }

    @Test
    public void deposit_shouldDataNotFoundWhenAccountIsNotFound() {
        final var accountNumber = "234566";
        final var pin = "12345";
        final var transaction = getTransactionRequest(500);

        when(accountRepositoryMock.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        final var assertThrows =
            assertThrows(DataNotFoundException.class, () -> accountService.deposit(accountNumber, pin, transaction));

        assertEquals("Account number '%s' was not found".formatted(accountNumber), assertThrows.getMessage());

        verify(accountRepositoryMock, times(1)).findByAccountNumber(anyString());
        verify(accountRepositoryMock, times(0)).save(any(AccountEntity.class));
    }

    @Test
    public void deposit_shouldBadRequestWhenPinIsInvalid() {
        final var accountNumber = "234566";
        final var pin = "12345";
        final var transaction = getTransactionRequest(500);

        final var returnedAccount =
            getAccountEntity(accountNumber, "4321", 800, 200);

        when(accountRepositoryMock.findByAccountNumber(accountNumber))
            .thenReturn(Optional.of(returnedAccount));

        final var assertThrows =
            assertThrows(BadRequestException.class, () -> accountService.deposit(accountNumber, pin, transaction));

        assertEquals("Pin account is invalid!", assertThrows.getMessage());

        verify(accountRepositoryMock, times(1)).findByAccountNumber(anyString());
        verify(accountRepositoryMock, times(0)).save(any(AccountEntity.class));
    }
}
