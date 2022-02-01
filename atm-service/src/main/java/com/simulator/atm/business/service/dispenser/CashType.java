package com.simulator.atm.business.service.dispenser;

import java.math.BigDecimal;

public enum CashType {
  FIVE(new BigDecimal(5)),
  TEN(new BigDecimal(10)),
  TWENTY(new BigDecimal(20)),
  FIFTY(new BigDecimal(50));

  private final BigDecimal value;

  CashType(BigDecimal amount) {
    value = amount;
  }

  public BigDecimal value() {
    return value;
  }
}
