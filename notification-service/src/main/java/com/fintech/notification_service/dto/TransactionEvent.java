package com.fintech.notification_service.dto;

import java.math.BigDecimal;

public record TransactionEvent(
        String referenceId,
        Long accountId,
        BigDecimal amount,
        String type,
        String status,
        String message
){}
