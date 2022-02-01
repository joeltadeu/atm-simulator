package com.simulator.account.business.service;

import com.simulator.account.business.persistence.entity.AccountEntity;
import com.simulator.account.business.persistence.repository.AccountRepository;
import com.simulator.dto.TransactionRequest;
import com.simulator.exception.BadRequestException;
import com.simulator.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class AccountService {

  private final AccountRepository repository;

  public AccountService(AccountRepository repository) {
    this.repository = repository;
  }

  public void withdraw(String accountNumber, String pin, TransactionRequest request) {
    log.info(
        "Withdraw funds: account number: [{}], amount: [{}]", accountNumber, request.getAmount());
    AccountEntity accountEntity = findByAccountNumber(accountNumber);

    log.info("Checking pin account...");
    checkPin(pin, accountEntity);

    log.info("Checking balance...");
    BigDecimal totalBalance =
        accountEntity.getBalance().add(accountEntity.getOverdraft()).subtract(request.getAmount());
    if (totalBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new BadRequestException("Your Account has insufficient funds to complete this request");
    }

    BigDecimal updatedBalance = accountEntity.getBalance().subtract(request.getAmount());
    accountEntity.setBalance(updatedBalance);

    log.info("Saving account with new balance: [{}]", updatedBalance);
    repository.save(accountEntity);
  }

  public void deposit(String accountNumber, String pin, TransactionRequest request) {
    log.info(
        "Deposit funds: account number: [{}], amount: [{}]", accountNumber, request.getAmount());
    AccountEntity accountEntity = findByAccountNumber(accountNumber);

    log.info("Checking pin account...");
    checkPin(pin, accountEntity);

    log.info("Calculating new balance...");
    BigDecimal balance = accountEntity.getBalance().add(request.getAmount());

    accountEntity.setBalance(balance);

    log.info("Saving account with new balance: [{}]", balance);
    repository.save(accountEntity);
  }

  public AccountEntity findByAccountNumber(String accountNumber) {
    log.info("Account number: [{}]", accountNumber);
    Optional<AccountEntity> accountEntityOptional = repository.findByAccountNumber(accountNumber);

    if (accountEntityOptional.isEmpty()) {
      throw new DataNotFoundException(
          String.format("Account number '%s' was not found", accountNumber));
    }

    return accountEntityOptional.get();
  }

  public AccountEntity balance(String accountNumber, String pin) {
    log.info(
        "Balance account: account number: [{}]", accountNumber);
    AccountEntity accountEntity = findByAccountNumber(accountNumber);

    log.info("Checking pin account...");
    checkPin(pin, accountEntity);

    return accountEntity;
  }

  private void checkPin(String pin, AccountEntity accountEntity) {
    if(! accountEntity.getPin().equals(pin))
      throw new BadRequestException("Pin account is invalid!");
  }
}
