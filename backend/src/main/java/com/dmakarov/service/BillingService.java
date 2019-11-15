package com.dmakarov.service;

import com.dmakarov.model.AccountBalance;
import com.dmakarov.model.Transaction;
import com.dmakarov.model.TransactionBody;
import java.util.List;
import java.util.UUID;

public interface BillingService {
  Transaction createTransaction(TransactionBody transaction);

  List<Transaction> getTransactionHistory();

  Transaction getTransaction(UUID transactionId);

  UUID createAccount();

  AccountBalance getAccountBalance(UUID accountId);
}
