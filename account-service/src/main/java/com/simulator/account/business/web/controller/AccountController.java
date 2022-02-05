package com.simulator.account.business.web.controller;

import com.simulator.account.business.persistence.entity.AccountEntity;
import com.simulator.account.business.service.AccountService;
import com.simulator.account.business.web.helper.AccountHelper;
import com.simulator.dto.AccountBalanceDto;
import com.simulator.dto.TransactionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/v1/accounts")
@Slf4j
@Tag(
    name = "Account Api",
    description =
        "This service is responsible for managing accounts. A member can do a withdraw, get a balance and a debit.")
public class AccountController {

  private final AccountService service;
  private final AccountHelper helper;

  public AccountController(AccountService service, AccountHelper helper) {
    this.service = service;
    this.helper = helper;
  }

  @GetMapping(value = "/{accountNumber}/balance", produces = "application/json")
  @Operation(summary = "Get balance from account")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the book",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AccountEntity.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid account number supplied",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pin supplied",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Account number not found",
            content = @Content)
      })
  public ResponseEntity<AccountBalanceDto> find(
      @Parameter(description = "Account number to be searched") @PathVariable String accountNumber,
      @Parameter(description = "Pin of account")
          @RequestHeader(value = "pin")
          @NotNull(message = "Missing pin")
          String pin) {
    log.info("Finding account by account number: {}", accountNumber);
    AccountEntity entity = service.balance(accountNumber, pin);
    log.info("Account found: [{}]", entity);
    return ResponseEntity.ok(helper.toModel(entity));
  }

  @PutMapping(path = "/{accountNumber}/withdraw")
  @Operation(summary = "Withdraw a funds")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Withdraw funds request completed successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid account number supplied",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pin supplied",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Account number not found",
            content = @Content)
      })
  public ResponseEntity<Void> withdraw(
      @Parameter(name = "Account number", example = "328762", required = true)
          @PathVariable(value = "accountNumber")
          String accountNumber,
      @Parameter(description = "Pin of account", example = "1234", required = true)
          @RequestHeader(value = "pin")
          @NotNull(message = "Missing pin")
          String pin,
      @RequestBody @Valid TransactionRequest request) {

    service.withdraw(accountNumber, pin, request);
    return ResponseEntity.ok().build();
  }

  @PutMapping(path = "/{accountNumber}/deposit")
  @Operation(summary = "Deposit a funds")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Deposit funds request completed successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid account number supplied",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pin supplied",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Account number not found",
            content = @Content)
      })
  public ResponseEntity<Void> deposit(
      @Parameter(name = "Account number", example = "328762", required = true)
          @PathVariable(value = "accountNumber")
          String accountNumber,
      @Parameter(description = "Pin of account", example = "1234", required = true)
          @RequestHeader(value = "pin")
          @NotNull(message = "Missing pin")
          String pin,
      @RequestBody @Valid TransactionRequest request) {

    log.info(
        "Request for deposit funds, account number {}, amount {}",
        accountNumber,
        request.getAmount());
    service.deposit(accountNumber, pin, request);
    return ResponseEntity.ok().build();
  }
}
