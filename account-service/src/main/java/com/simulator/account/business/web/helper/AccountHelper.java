package com.simulator.account.business.web.helper;

import com.simulator.account.business.persistence.entity.AccountEntity;
import com.simulator.dto.AccountBalanceDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AccountHelper {
  private final ModelMapper modelMapper;

  public AccountHelper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  public AccountBalanceDto toModel(AccountEntity entity) {
    return modelMapper.map(entity, AccountBalanceDto.class);
  }
}
