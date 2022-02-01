package com.simulator.account.business.persistence.repository;

import com.simulator.account.business.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

  Optional<AccountEntity> findByAccountNumberAndPin(String accountNumber, String pin);

  Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
