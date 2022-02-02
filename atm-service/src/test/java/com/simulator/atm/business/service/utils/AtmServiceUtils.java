package com.simulator.atm.business.service.utils;

import com.simulator.atm.business.service.dispenser.Cash;
import com.simulator.atm.business.service.dispenser.CashType;
import com.simulator.exception.BadRequestException;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.RetryableException;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AtmServiceUtils {

  public static RetryableException getRetryableExceptionPinIsInvalid() {
    Request request =
        Request.create(Request.HttpMethod.GET, "url", new HashMap<>(), null, new RequestTemplate());

    return new RetryableException(
        HttpStatus.BAD_REQUEST.value(),
        "Pin account is invalid!",
        Request.HttpMethod.GET,
        new Date(),
        request);
  }

  public static BadRequestException getBadRequestExceptionAtmHasNoCash() {
    return new BadRequestException("Atm does not have the funds to complete your request");
  }

  public static BadRequestException getBadRequestExceptionAtmCanNotGiveAmount() {
    return new BadRequestException("It is not possible to dispense this value");
  }

  public static FeignException getRetryableExceptionAccountIsNotFound(String accountNumber) {
    Request request =
        Request.create(Request.HttpMethod.GET, "url", new HashMap<>(), null, new RequestTemplate());

    return new FeignException.NotFound(
        "Account number '%s' was not found".formatted(accountNumber), request, null, null);
  }

  public static FeignException getRetryableExceptionAccountHasInsufficientFunds() {
    Request request =
        Request.create(Request.HttpMethod.GET, "url", new HashMap<>(), null, new RequestTemplate());

    return new FeignException.BadRequest(
        "Your Account has insufficient funds to complete this request", request, null, null);
  }

  public static List<Cash> getCashDispensed() {
    return List.of(
        new Cash(CashType.FIFTY, 2, null),
        new Cash(CashType.TWENTY, 2, null),
        new Cash(CashType.TWENTY, 1, null));
  }
}
