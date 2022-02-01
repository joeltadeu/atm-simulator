package com.simulator.account.business.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "account")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@Entity
public class AccountEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "account_number")
  private String accountNumber;

  private String pin;

  private BigDecimal balance;

  private BigDecimal overdraft;
}
