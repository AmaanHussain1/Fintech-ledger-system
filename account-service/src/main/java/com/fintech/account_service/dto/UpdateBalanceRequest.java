package com.fintech.account_service.dto;

import java.math.BigDecimal;

public record UpdateBalanceRequest(BigDecimal amount) {
}
