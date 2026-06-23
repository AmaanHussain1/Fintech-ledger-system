package com.fintech.transaction_service.dto;

import com.fintech.transaction_service.entity.TransactionType;

import java.math.BigDecimal;

public record TransactionRequest(
        Long accountId,
        BigDecimal amount,
        TransactionType type
) {}
