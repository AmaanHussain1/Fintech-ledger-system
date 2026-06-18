package com.fintech.account_service.dto;

public record CreateAccountRequest(
        Long userId,
        String currency
) {}
