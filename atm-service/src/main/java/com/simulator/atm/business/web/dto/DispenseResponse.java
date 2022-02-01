package com.simulator.atm.business.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispenseResponse {

  @Schema(description = "Cash dispensed", name = "dispensedCash", example = "1000")
  private Integer dispensedCash;

  private List<CashDispensedDto> notes;
}
