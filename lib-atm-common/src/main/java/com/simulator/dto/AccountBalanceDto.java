package com.simulator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDto {

  @Schema(description = "Balance Account", name = "balance", example = "10,891.00")
  private BigDecimal balance;

  @Schema(description = "Overdraft Account", name = "overdraft", example = "25,000.00")
  private BigDecimal overdraft;
}
