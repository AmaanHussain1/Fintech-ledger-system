package com.fintech.notification_service.service;

import com.fintech.notification_service.dto.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumer {
    @KafkaListener( topics = "transaction-events", groupId = "notification-group-v2" )
    public void consumeTransactionEvent(TransactionEvent event){
        log.info("=========================================");
        log.info("🔔 NOTIFICATION ALERT TRIGGERED");
        log.info("=========================================");
        log.info("Sending Email to Account ID : {}", event.accountId());
        log.info("Transaction Type            : {}", event.type());
        log.info("Amount                      : ${}", event.amount());
        log.info("Status                      : {}", event.status());
        log.info("System Message              : {}", event.message());
        log.info("Reference ID                : {}", event.referenceId());
        log.info("=========================================\n");
    }
}
