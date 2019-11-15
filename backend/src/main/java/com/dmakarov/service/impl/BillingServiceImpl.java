package com.dmakarov.service.impl;

import com.dmakarov.model.AccountBalance;
import com.dmakarov.model.Transaction;
import com.dmakarov.model.TransactionBody;
import com.dmakarov.model.exception.BillingException;
import com.dmakarov.service.BillingService;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BillingServiceImpl implements BillingService {

  private final ConcurrentHashMap<UUID, AccountBalance> accounts = new ConcurrentHashMap<>();
  private final LinkedHashMap<UUID, Transaction> transactions = new LinkedHashMap<>();

  @Override
  public Transaction createTransaction(TransactionBody transactionBody) {
    AccountBalance account = getAccount(transactionBody.getAccountId());
    double newAmount = account.getAmount() + transactionBody.getAmount();

    if (newAmount < 0) {
      throw new BillingException(HttpStatus.BAD_REQUEST, "Negative balance");
    }

    UUID transactionId = UUID.randomUUID();
    Transaction transaction = Transaction.builder()
        .transactionId(transactionId)
        .accountId(transactionBody.getAccountId())
        .amount(transactionBody.getAmount())
        .type(transactionBody.getType())
        .effectiveDate(new Date().getTime())
        .build();
    transactions.put(transactionId, transaction);

    accounts.put(account.getAccountId(), AccountBalance.builder()
        .accountId(account.getAccountId())
        .amount(newAmount)
        .build());

    return transaction;
  }

  @Override
  public List<Transaction> getTransactionHistory() {
    return new ArrayList<>(transactions.values());
  }

  @Override
  public Transaction getTransaction(UUID transactionId) {
    return transactions.get(transactionId);
  }

  @Override
  public UUID createAccount() {
    UUID accountId = UUID.randomUUID();
    accounts.put(accountId, AccountBalance.builder()
        .accountId(accountId)
        .amount(0.0D)
        .build());

    return accountId;
  }

  private AccountBalance getAccount(UUID accountId) {
    AccountBalance account = accounts.get(accountId);
    if (account == null) {
      throw new BillingException(HttpStatus.NOT_FOUND, "Account not found");
    }

    return account;
  }

  @Override
  public AccountBalance getAccountBalance(UUID accountId) {
    AccountBalance account = getAccount(accountId);

    return AccountBalance.builder()
        .accountId(accountId)
        .amount(account.getAmount())
        .build();
  }
}
