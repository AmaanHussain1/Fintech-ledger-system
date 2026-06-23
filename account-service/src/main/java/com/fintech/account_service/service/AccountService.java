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

    @Transactional
    public Account updateBalance(Long userId, BigDecimal amount) {
        // Fetch the account AND lock the database row
        Account account = accountRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new RuntimeException("Account not found for user ID: " + userId));

        // Calculate the new balance safely
        BigDecimal newBalance = account.getBalance().add(amount);

        // No negative balances allowed
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient funds for user ID: " + userId);
        }

        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
}
