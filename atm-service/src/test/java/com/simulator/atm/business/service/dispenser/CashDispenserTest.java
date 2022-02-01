package com.simulator.atm.business.service.dispenser;

import com.simulator.exception.BadRequestException;
import com.simulator.exception.DataNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CashDispenserTest {

  @InjectMocks private CashDispenser cashDispenser;

  @Test
  public void getTotalCash_shouldHas1500InCashWhenAtmStarted() {
    final var totalCash = cashDispenser.getTotalCash();
    assertEquals(totalCash, 1500);
  }

  @Test
  public void getTotalCash_shouldHasZeroInCashWhenAtmHasNoMoreCash() {
    cashDispenser.dispense(1500);
    final var totalCash = cashDispenser.getTotalCash();
    assertEquals(totalCash, 0);
  }

  @Test
  public void dispense_shouldDispenseWhenAmountIs1410() {
    final var cashedDispensed = cashDispenser.dispense(1410);

    assertEquals(cashedDispensed.get(0).getType(), CashType.FIFTY);
    assertEquals(cashedDispensed.get(0).getTotal(), 10);
    assertEquals(cashedDispensed.get(1).getType(), CashType.TWENTY);
    assertEquals(cashedDispensed.get(1).getTotal(), 30);
    assertEquals(cashedDispensed.get(2).getType(), CashType.TEN);
    assertEquals(cashedDispensed.get(2).getTotal(), 30);
    assertEquals(cashedDispensed.get(3).getType(), CashType.FIVE);
    assertEquals(cashedDispensed.get(3).getTotal(), 2);

    assertEquals(cashDispenser.getTotalCash(), 90);
  }

  @Test
  public void dispense_shouldDispenseWhenAmountIs600() {
    final var cashedDispensed = cashDispenser.dispense(600);

    assertEquals(cashedDispensed.get(0).getType(), CashType.FIFTY);
    assertEquals(cashedDispensed.get(0).getTotal(), 10);
    assertEquals(cashedDispensed.get(1).getType(), CashType.TWENTY);
    assertEquals(cashedDispensed.get(1).getTotal(), 5);

    assertEquals(cashDispenser.getTotalCash(), 900);
  }

  @Test
  public void dispense_shouldDispenseWhenAmountIs245() {
    final var cashedDispensed = cashDispenser.dispense(245);

    assertEquals(cashedDispensed.get(0).getType(), CashType.FIFTY);
    assertEquals(cashedDispensed.get(0).getTotal(), 4);
    assertEquals(cashedDispensed.get(1).getType(), CashType.TWENTY);
    assertEquals(cashedDispensed.get(1).getTotal(), 2);
    assertEquals(cashedDispensed.get(2).getType(), CashType.TEN);
    assertEquals(cashedDispensed.get(2).getTotal(), 0);
    assertEquals(cashedDispensed.get(3).getType(), CashType.FIVE);
    assertEquals(cashedDispensed.get(3).getTotal(), 1);

    assertEquals(cashDispenser.getTotalCash(), 1255);
  }

  @Test
  public void dispense_shouldDispenseWhenAmountIs1500() {
    final var cashedDispensed = cashDispenser.dispense(1500);

    assertEquals(cashedDispensed.get(0).getType(), CashType.FIFTY);
    assertEquals(cashedDispensed.get(0).getTotal(), 10);
    assertEquals(cashedDispensed.get(1).getType(), CashType.TWENTY);
    assertEquals(cashedDispensed.get(1).getTotal(), 30);
    assertEquals(cashedDispensed.get(2).getType(), CashType.TEN);
    assertEquals(cashedDispensed.get(2).getTotal(), 30);
    assertEquals(cashedDispensed.get(3).getType(), CashType.FIVE);
    assertEquals(cashedDispensed.get(3).getTotal(), 20);

    assertEquals(cashDispenser.getTotalCash(), 0);
  }

  @Test
  public void dispense_shouldNotDispenseWhenAtmHasNoCash() {
    final var assertThrows =
        assertThrows(BadRequestException.class, () -> cashDispenser.dispense(2000));

    assertEquals("Atm does not have the funds to complete your request", assertThrows.getMessage());
  }

  @Test
  public void dispense_shouldNotDispenseWhenAmountIsNotDivisibleByFive() {
    final var assertThrows =
        assertThrows(BadRequestException.class, () -> cashDispenser.dispense(143));

    assertEquals("It is not possible to dispense this value", assertThrows.getMessage());
  }
}
