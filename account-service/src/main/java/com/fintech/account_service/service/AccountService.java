package com.fintech.account_service.service;

import com.fintech.account_service.enitity.Account;
import com.fintech.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Account createAccount(Long userId, String currency) {
        accountRepository.findByUserId(userId).ifPresent(account -> {
            throw new RuntimeException("Account already exists for user ID:" + userId);
        });

        Account newAccount = Account.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .currency(currency.toUpperCase())
                .build();

        return accountRepository.save(newAccount);
    }

    @Transactional(readOnly = true)
    public Account getAccountByUserId(Long userId){
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found for user ID: " + userId));
    }
}
