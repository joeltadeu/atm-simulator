package com.simulator.atm.business.service;

import com.simulator.atm.business.service.client.AccountServiceClient;
import com.simulator.atm.business.service.dispenser.Cash;
import com.simulator.atm.business.service.dispenser.CashDispenser;
import com.simulator.dto.AccountBalanceDto;
import com.simulator.dto.TransactionRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class AtmService {

  private final AccountServiceClient client;
  private final CashDispenser cashDispenser;

  public AtmService(AccountServiceClient client, CashDispenser cashDispenser) {
    this.client = client;
    this.cashDispenser = cashDispenser;
  }

  public List<Cash> dispense(String accountNumber, String pin, Integer amount) {
    log.info("Dispense cash: account number: [{}], amount: [{}]", accountNumber, amount);

    final var cashedDispensed = cashDispenser.dispense(amount);

    final var transaction = TransactionRequest.builder().amount(BigDecimal.valueOf(amount)).build();

    try {
      log.info("Call account-api :: withdraw funds");
      client.withdraw(accountNumber, pin, transaction);
    } catch (FeignException e) {
      log.error("Error during withdraw process :: refund cash to ATM machine...");
      cashDispenser.refund(cashedDispensed);
      throw e;
    }
    return cashedDispensed;
  }

  public AccountBalanceDto balance(String accountNumber, String pin) {
    log.info("Get Balance: account number: [{}]", accountNumber);
    return client.balance(accountNumber, pin);
  }
}
