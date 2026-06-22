package com.fintech.transaction_service.service;

import com.fintech.transaction_service.client.AccountClient;
import com.fintech.transaction_service.dto.AccountDto;
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
    private final AccountClient accountClient;

    @Transactional
    public Transaction processTransaction(TransactionRequest request){

        // Inter-Service Communication: Fetch the account over HTTP
        AccountDto account = accountClient.getAccount(request.accountId());

        Transaction transaction = Transaction.builder()
                .accountId(account.id())
                .amount(request.amount())
                .type(request.type())
                .status(TransactionStatus.PENDING)
                .referenceId(UUID.randomUUID().toString())
                .build();

        return transactionRepository.save(transaction);
    }
}
