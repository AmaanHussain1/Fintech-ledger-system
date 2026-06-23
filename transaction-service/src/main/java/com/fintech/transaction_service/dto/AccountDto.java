package com.fintech.transaction_service.dto;

import java.math.BigDecimal;

// Maps the JSON response from the account-service into a Java object.
public record AccountDto(
        Long id,
        Long userId,
        BigDecimal balance,
        String currency
) {}
