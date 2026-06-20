package com.fintech.transaction_service.service;

import com.fintech.transaction_service.dto.TransactionRequest;
import com.fintech.transaction_service.entity.Transaction;
import com.fintech.transaction_service.entity.TransactionStatus;
import com.fintech.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction processTransaction(TransactionRequest request){
        Transaction transaction = Transaction.builder()
                .accountId(request.accountId())
                .amount(request.amount())
                .type(request.type())
                .status(TransactionStatus.PENDING)
                .referenceId(UUID.randomUUID().toString())
                .build();

        transaction = transactionRepository.save(transaction);

        return transaction;
    }
}
