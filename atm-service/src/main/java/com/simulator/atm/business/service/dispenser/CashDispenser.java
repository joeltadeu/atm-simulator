package com.simulator.atm.business.service.dispenser;

import com.simulator.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CashDispenser {
  private final Cash notesChain;

  public CashDispenser() {
    Cash fiveCash = new Cash(CashType.FIVE, 20, null);
    Cash tenCash = new Cash(CashType.TEN, 30, fiveCash);
    Cash twentyCash = new Cash(CashType.TWENTY, 30, tenCash);
    this.notesChain = new Cash(CashType.FIFTY, 10, twentyCash);
    log.info("Initializing cash dispenser :: {}", notesChain);
  }

  public Integer getTotalCash() {
    return calculateCash(this.notesChain);
  }

  private Integer calculateCash(Cash notesChain) {
    if (notesChain == null) return 0;
    return notesChain.getTotal() * notesChain.getType().value().intValue()
        + calculateCash(notesChain.getNextChain());
  }

  public List<Cash> dispense(Integer amount) {
    if (amount > getTotalCash())
      throw new BadRequestException("Atm does not have the funds to complete your request");

    if (amount % 5 > 0) {
      throw new BadRequestException("It is not possible to dispense this value");
    }

    List<Cash> dispensed = new ArrayList<>();
    dispenseMoney(amount, notesChain, dispensed);
    return dispensed;
  }

  private void dispenseMoney(Integer amount, Cash cash, List<Cash> dispensed) {
    if (cash == null) return;
    log.info("Dispense cash :: amount {}, cash type {}, notes {}", amount, cash.getType(), cash.getTotal());
    var currentNotesNumber = cash.getType().value().intValue();
    if (cash.getTotal() > 0) {
      int num = Math.min(amount / currentNotesNumber, cash.getTotal());
      log.info("Notes dispensed: {}", num);
      amount -= num * currentNotesNumber;
      cash.subtract(num);
      dispensed.add(new Cash(cash.getType(), num, null));
      if (amount == 0) return;
    }
    dispenseMoney(amount, cash.getNextChain(), dispensed);
  }

  public void refund(List<Cash> cashedDispensed) {
    cashedDispensed.forEach(
        e -> {
          refundMoney(notesChain, e);
        });
  }

  private void refundMoney(Cash currentCash, Cash cashToRefund) {
    if (currentCash == null) return;
    log.info("Cash to be refunded : cash type {}, notes {}", cashToRefund.getType(), cashToRefund.getTotal());
    if (currentCash.getType().equals(cashToRefund.getType())) {
      currentCash.add(cashToRefund.getTotal());
    } else {
      refundMoney(currentCash.getNextChain(), cashToRefund);
    }
  }
}
