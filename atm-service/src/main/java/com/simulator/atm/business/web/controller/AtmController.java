package com.simulator.atm.business.web.controller;

import com.simulator.atm.business.service.AtmService;
import com.simulator.atm.business.service.dispenser.Cash;
import com.simulator.atm.business.web.dto.DispenseResponse;
import com.simulator.atm.business.web.helper.AtmHelper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/atm")
@Slf4j
@Tag(
    name = "Atm Api",
    description =
        "This service is responsible for managing atm. A user can do dispense money and get a balance.")
public class AtmController {

  private final AtmService service;
  private final AtmHelper helper;

  public AtmController(AtmService service, AtmHelper helper) {
    this.service = service;
    this.helper = helper;
  }

  @PostMapping(path = "/dispense")
  @Operation(summary = "Dispense amount requested by the user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Dispense funds request completed successfully"),
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
  public ResponseEntity<DispenseResponse> dispense(
      @Parameter(description = "Account number", example = "328762", required = true)
          @RequestHeader(value = "accountNumber")
          @NotNull(message = "Missing account number")
          String accountNumber,
      @Parameter(description = "Pin of account", example = "1234", required = true)
          @RequestHeader(value = "pin")
          @NotNull(message = "Missing pin")
          String pin,
      @RequestBody @Valid TransactionRequest request) {

    log.info("Request for dispense cash €{}", request.getAmount());
    List<Cash> cashDispensed = service.dispense(accountNumber, pin, request.getAmount().intValue());

    DispenseResponse response =
        DispenseResponse.builder()
            .dispensedCash(request.getAmount().intValue())
            .notes(helper.toModel(cashDispensed))
            .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/balance", produces = "application/json")
  @Operation(summary = "Get balance from account")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Return a account balance",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AccountBalanceDto.class))
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
  public ResponseEntity<AccountBalanceDto> balance(
      @Parameter(description = "Account number")
          @RequestHeader(value = "accountNumber")
          @NotNull(message = "Missing account number")
          String accountNumber,
      @Parameter(description = "Pin of account")
          @RequestHeader(value = "pin")
          @NotNull(message = "Missing pin")
          String pin) {
    log.info("Request for get balance by account number {}", accountNumber);
    AccountBalanceDto accountBalanceDto = service.balance(accountNumber, pin);
    return ResponseEntity.ok(accountBalanceDto);
  }
}
