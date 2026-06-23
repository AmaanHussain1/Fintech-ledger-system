package com.fintech.account_service.controller;

import com.fintech.account_service.dto.CreateAccountRequest;
import com.fintech.account_service.enitity.Account;
import com.fintech.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request){
        Account newAccount = accountService.createAccount(request.userId(), request.currency());
        return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long userId){
        Account account = accountService.getAccountByUserId(userId);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{userId}/balance")
    public ResponseEntity<Account> updateBalance(@PathVariable Long userId,
                                                 @RequestBody com.fintech.account_service.dto
                                                         .UpdateBalanceRequest request){
        Account updateAccount = accountService.updateBalance(userId, request.amount());
        return ResponseEntity.ok(updateAccount);
    }
}
