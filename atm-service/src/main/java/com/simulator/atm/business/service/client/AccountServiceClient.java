package com.simulator.atm.business.service.client;

import com.simulator.dto.AccountBalanceDto;
import com.simulator.dto.TransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Component
@FeignClient(url = "${account-api-url}", name = "AccountClient")
public interface AccountServiceClient {

  @PutMapping(value = "/v1/accounts/{accountNumber}/withdraw")
  void withdraw(
      @PathVariable("accountNumber") String accountNumber,
      @RequestHeader(value = "pin", name = "pin") String pin,
      @RequestBody TransactionRequest request);

  @GetMapping(value = "/v1/accounts/{accountNumber}/balance")
  AccountBalanceDto balance(
      @PathVariable("accountNumber") String accountNumber,
      @RequestHeader(value = "pin", name = "pin") String pin);
}
