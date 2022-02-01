package com.simulator.atm.business.service.dispenser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Cash {
  private CashType type;
  private int total;
  private Cash nextChain;

  public void subtract(int value) {
    this.total -= value;
  }
}
