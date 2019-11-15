package com.dmakarov.starter;

import com.dmakarov.model.TransactionBody;
import com.dmakarov.model.TransactionType;
import com.dmakarov.service.BillingService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Application runner which loads default accounts.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DefaultAccountsStarter implements ApplicationRunner {

  private final BillingService service;

  @Override
  public void run(ApplicationArguments args) {
    UUID account1 = service.createAccount();
    UUID account2 = service.createAccount();
    UUID account3 = service.createAccount();

    log.info("Test account initialized {}", account1.toString());
    log.info("Test account initialized {}", account2.toString());
    log.info("Test account initialized {}", account3.toString());

    service.createTransaction(TransactionBody.builder()
        .accountId(account1)
        .type(TransactionType.CREDIT)
        .amount(10.0D)
        .build()
    );

    service.createTransaction(TransactionBody.builder()
        .accountId(account1)
        .type(TransactionType.DEBIT)
        .amount(-5.0D)
        .build()
    );

    service.createTransaction(TransactionBody.builder()
        .accountId(account2)
        .type(TransactionType.CREDIT)
        .amount(15.0D)
        .build()
    );

    service.createTransaction(TransactionBody.builder()
        .accountId(account3)
        .type(TransactionType.CREDIT)
        .amount(25.0D)
        .build()
    );
  }

}
