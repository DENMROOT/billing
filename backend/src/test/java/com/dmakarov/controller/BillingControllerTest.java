package com.dmakarov.controller;

import static com.dmakarov.ApiPathsV1.ACCOUNT;
import static com.dmakarov.ApiPathsV1.HISTORY;
import static com.dmakarov.ApiPathsV1.ROOT;
import static com.dmakarov.ApiPathsV1.TRANSACTIONS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dmakarov.model.AccountBalance;
import com.dmakarov.model.Transaction;
import com.dmakarov.model.TransactionBody;
import com.dmakarov.model.TransactionType;
import com.dmakarov.model.exception.BillingException;
import com.dmakarov.service.BillingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureMockMvc
class BillingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BillingService service;

  @Test
  void createTransaction() throws Exception {
    UUID accountId = UUID.randomUUID();
    double amount = 10.0D;
    TransactionBody transactionBody = TransactionBody.builder()
        .accountId(accountId)
        .type(TransactionType.CREDIT)
        .amount(amount)
        .build();
    Transaction transaction = Transaction.builder()
        .accountId(accountId)
        .amount(amount)
        .type(TransactionType.CREDIT)
        .build();

    when(service.createTransaction(any(TransactionBody.class)))
        .thenReturn(transaction);

    this.mockMvc
        .perform(
            post(ROOT + TRANSACTIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(transactionBody))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId")
            .value(transactionBody.getAccountId().toString()))
        .andExpect(jsonPath("$.type").value(transactionBody.getType().toString()))
        .andExpect(jsonPath("$.amount").value(transactionBody.getAmount()));
  }

  @Test
  void getTransaction() throws Exception {
    UUID transactionId = UUID.randomUUID();
    UUID accountId = UUID.randomUUID();
    double amount = 10.0D;
    Transaction transaction = Transaction.builder()
        .transactionId(transactionId)
        .accountId(accountId)
        .amount(amount)
        .type(TransactionType.CREDIT)
        .build();

    when(service.getTransaction(any(UUID.class))).thenReturn(transaction);

    this.mockMvc
        .perform(
            get(ROOT + TRANSACTIONS + "/{transactionId}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId")
            .value(transaction.getAccountId().toString()))
        .andExpect(jsonPath("$.transactionId")
            .value(transaction.getTransactionId().toString()))
        .andExpect(jsonPath("$.type").value(transaction.getType().toString()))
        .andExpect(jsonPath("$.amount").value(transaction.getAmount()));
  }

  @Test
  void getTransactions() throws Exception {
    UUID accountId = UUID.randomUUID();
    double amount = 10.0D;
    Transaction creditTransaction = Transaction.builder()
        .transactionId(UUID.randomUUID())
        .accountId(accountId)
        .amount(amount)
        .type(TransactionType.CREDIT)
        .build();
    Transaction debitTransaction = Transaction.builder()
        .transactionId(UUID.randomUUID())
        .accountId(accountId)
        .amount(amount)
        .type(TransactionType.DEBIT)
        .build();
    when(service.getTransactionHistory())
        .thenReturn(Arrays.asList(creditTransaction, debitTransaction));

    this.mockMvc
        .perform(
            get(ROOT + TRANSACTIONS + HISTORY)
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].transactionId")
            .value(creditTransaction.getTransactionId().toString()))
        .andExpect(jsonPath("$[0].accountId").value(accountId.toString()))
        .andExpect(jsonPath("$[1].transactionId")
            .value(debitTransaction.getTransactionId().toString()))
        .andExpect(jsonPath("$[1].accountId").value(accountId.toString()));
  }

  @Test
  void getAccount() throws Exception {
    UUID accountId = UUID.randomUUID();
    double amount = 10.0D;
    AccountBalance accountBalance = AccountBalance.builder()
        .accountId(accountId)
        .amount(amount)
        .build();

    when(service.getAccountBalance(any(UUID.class))).thenReturn(accountBalance);

    this.mockMvc
        .perform(
            get(ROOT + ACCOUNT + "/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId")
            .value(accountBalance.getAccountId().toString()))
        .andExpect(jsonPath("$.amount").value(accountBalance.getAmount()));
  }

  @Test
  void getAccount_notFound() throws Exception {
    UUID accountId = UUID.randomUUID();

    when(service.getAccountBalance(any(UUID.class)))
        .thenThrow(new BillingException(HttpStatus.NOT_FOUND, "not found"));

    this.mockMvc
        .perform(
            get(ROOT + ACCOUNT + "/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  /**
   * Serializes Object to Json string representation.
   *
   * @param obj serialized object
   * @return json representation
   */
  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
