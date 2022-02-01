package com.simulator.atm.business.web.helper;

import com.simulator.atm.business.service.dispenser.Cash;
import com.simulator.atm.business.web.dto.CashDispensedDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class AtmHelper {
  private final ModelMapper modelMapper;

  public AtmHelper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  public CashDispensedDto toModel(Cash cash) {
    return modelMapper.map(cash, CashDispensedDto.class);
  }

  public List<CashDispensedDto> toModel(List<Cash> cashList) {
    return cashList.stream().map(this::toModel).collect(toList());
  }
}
