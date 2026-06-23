package com.fintech.transaction_service.dto;

import java.math.BigDecimal;

public record UpdateBalanceRequest(BigDecimal amount) {}
