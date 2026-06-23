package com.fintech.transaction_service.controller;

import com.fintech.transaction_service.dto.TransactionRequest;
import com.fintech.transaction_service.entity.Transaction;
import com.fintech.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequest request){
        Transaction transaction = transactionService.processTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
}
