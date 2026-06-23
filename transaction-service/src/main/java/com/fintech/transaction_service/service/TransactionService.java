package com.fintech.transaction_service.service;

import com.fintech.transaction_service.client.AccountClient;
import com.fintech.transaction_service.dto.AccountDto;
import com.fintech.transaction_service.dto.TransactionRequest;
import com.fintech.transaction_service.dto.UpdateBalanceRequest;
import com.fintech.transaction_service.entity.Transaction;
import com.fintech.transaction_service.entity.TransactionStatus;
import com.fintech.transaction_service.entity.TransactionType;
import com.fintech.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;

    @Transactional
    public Transaction processTransaction(TransactionRequest request){

        // Inter-Service Communication: Fetch the account over HTTP
        AccountDto account = accountClient.getAccount(request.accountId());

        // Build the PENDING transaction
        Transaction transaction = Transaction.builder()
                .accountId(account.id())
                .amount(request.amount())
                .type(request.type())
                .status(TransactionStatus.PENDING)
                .referenceId(UUID.randomUUID().toString())
                .build();

        transaction = transactionRepository.save(transaction);

        // Determine if add or subtract money
        BigDecimal amountToApply = request.type() == TransactionType.WITHDRAWAL
                ? request.amount().negate()
                : request.amount();

        try{
            log.info("Attempting to update balance for user {} by amount {}", account.userId(), amountToApply);

            // Network call to the Account Service!
            accountClient.updateBalance(account.userId(), new UpdateBalanceRequest(amountToApply));

            transaction.setStatus(TransactionStatus.SUCCESS);
            log.info("Transaction {} completed successfully.", transaction.getReferenceId());
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            log.error("Transaction {} failed: {}", transaction.getReferenceId(), e.getMessage());
        }

        return transactionRepository.save(transaction);
    }
}
