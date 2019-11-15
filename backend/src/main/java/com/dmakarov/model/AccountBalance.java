package com.dmakarov.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountBalance {
  UUID accountId;
  Double amount;
}
