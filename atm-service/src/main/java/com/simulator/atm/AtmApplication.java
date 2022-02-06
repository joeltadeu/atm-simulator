package com.simulator.atm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AtmApplication {

  public static void main(String[] args) {
    SpringApplication.run(AtmApplication.class, args);
  }
}
