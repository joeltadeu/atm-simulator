package com.simulator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {
  @Schema(
      description = "Amount for the transaction (debit/withdraw)",
      name = "amount",
      example = "1,000.00")
  private BigDecimal amount;
}
