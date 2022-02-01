package com.simulator.atm.business.service;

import com.simulator.atm.business.service.client.AccountServiceClient;
import com.simulator.atm.business.service.dispenser.Cash;
import com.simulator.atm.business.service.dispenser.CashDispenser;
import com.simulator.dto.AccountBalanceDto;
import com.simulator.dto.TransactionRequest;
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

    var transaction =
        TransactionRequest.builder()
            .amount(BigDecimal.valueOf(amount))
            .build();

    client.withdraw(accountNumber, pin, transaction);
    return cashDispenser.dispense(amount);
  }

  public AccountBalanceDto balance(String accountNumber, String pin) {
    return client.balance(accountNumber, pin);
  }
}
