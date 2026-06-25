package com.fintech.transaction_service.service;

import com.fintech.transaction_service.client.AccountClient;
import com.fintech.transaction_service.dto.AccountDto;
import com.fintech.transaction_service.dto.TransactionEvent;
import com.fintech.transaction_service.dto.TransactionRequest;
import com.fintech.transaction_service.dto.UpdateBalanceRequest;
import com.fintech.transaction_service.entity.Transaction;
import com.fintech.transaction_service.entity.TransactionStatus;
import com.fintech.transaction_service.entity.TransactionType;
import com.fintech.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
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
    // Injected the Kafka Template to send messages
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Topic name
    private static final String TOPIC_TRANSACTION_EVENTS = "transaction-events";

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

        // Determine if to add or subtract money
        BigDecimal amountToApply = request.type() == TransactionType.WITHDRAWAL
                ? request.amount().negate() : request.amount();

        String eventMessage = "";

        try {
            log.info("Attempting to update balance for user {} by amount {}", account.userId(), amountToApply);
            accountClient.updateBalance(account.userId(), new UpdateBalanceRequest(amountToApply));

            transaction.setStatus(TransactionStatus.SUCCESS);
            eventMessage = "Transaction successful";
            log.info("Transaction {} completed successfully.", transaction.getReferenceId());

        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            eventMessage = "Transaction failed: " + e.getMessage();
            log.error("Transaction {} failed: {}", transaction.getReferenceId(), e.getMessage());
        }

        // Save the final status to DB
        Transaction savedTransaction = transactionRepository.save(transaction);

        TransactionEvent event = new TransactionEvent(
                savedTransaction.getReferenceId(),
                savedTransaction.getAccountId(),
                savedTransaction.getAmount(),
                savedTransaction.getType().name(),
                savedTransaction.getStatus().name(),
                eventMessage
        );

        kafkaTemplate.send(TOPIC_TRANSACTION_EVENTS, event);
        log.info("Published TransactionEvent to Kafka topic: {}", TOPIC_TRANSACTION_EVENTS);

        return savedTransaction;
    }
}
