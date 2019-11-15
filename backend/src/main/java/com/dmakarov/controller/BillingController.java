package com.dmakarov.controller;

import static com.dmakarov.ApiPathsV1.ACCOUNT;
import static com.dmakarov.ApiPathsV1.HISTORY;
import static com.dmakarov.ApiPathsV1.ROOT;
import static com.dmakarov.ApiPathsV1.TRANSACTIONS;

import com.dmakarov.model.AccountBalance;
import com.dmakarov.model.Transaction;
import com.dmakarov.model.TransactionBody;
import com.dmakarov.service.BillingService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(ROOT)
public class BillingController {

  private final BillingService service;

  @PostMapping(TRANSACTIONS)
  ResponseEntity<Transaction> createTransaction(@RequestBody TransactionBody transactionBody) {
    log.info("Create transaction request received, transaction {}", transactionBody);

    Transaction transaction = service.createTransaction(transactionBody);

    return ResponseEntity.ok().body(transaction);
  }

  @GetMapping(TRANSACTIONS + "/{transactionId}")
  ResponseEntity<Transaction> getTransaction(@PathVariable UUID transactionId) {
    log.info("Get transaction request received, transactionId {}", transactionId);

    Transaction transaction = service.getTransaction(transactionId);

    return ResponseEntity.ok().body(transaction);
  }

  @GetMapping(TRANSACTIONS + HISTORY)
  ResponseEntity<List<Transaction>> getTransactionHistory() {
    log.info("Get transaction history request received");

    List<Transaction> transactions = service.getTransactionHistory();

    return ResponseEntity.ok().body(transactions);
  }

  @GetMapping(ACCOUNT + "/{accountId}")
  ResponseEntity<AccountBalance> getAccountBalance(@PathVariable UUID accountId) {
    log.info("Get account balanse request received, transactionId {}", accountId);

    AccountBalance accountBalanse = service.getAccountBalance(accountId);

    return ResponseEntity.ok().body(accountBalanse);
  }
}
