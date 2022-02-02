package com.simulator.account.business.service.utils;

import com.simulator.account.business.persistence.entity.AccountEntity;
import com.simulator.dto.TransactionRequest;

import java.math.BigDecimal;

public class AccountServiceUtils {
  public static AccountEntity getAccountEntity(
      String accountNumber, String pin, Integer balance, Integer overdraft) {
    return AccountEntity.builder()
        .id(1L)
        .pin(pin)
        .accountNumber(accountNumber)
        .balance(BigDecimal.valueOf(balance))
        .overdraft(BigDecimal.valueOf(overdraft))
        .build();
  }

  public static TransactionRequest getTransactionRequest(Integer amount) {
    return TransactionRequest.builder().amount(BigDecimal.valueOf(amount)).build();
  }
}
