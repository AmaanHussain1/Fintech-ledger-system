package com.fintech.transaction_service.client;

import com.fintech.transaction_service.dto.AccountDto;
import com.fintech.transaction_service.dto.UpdateBalanceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

// The "name" is used for load balancing
@FeignClient(name = "account-service", url = "http://localhost:8081/api/v1/accounts")
public interface AccountClient {

    @GetMapping("/{userId}")
    AccountDto getAccount(@PathVariable("userId") Long userId);

    // Securely update the balance!
    @PutMapping("/{userId}/balance")
    AccountDto updateBalance(@PathVariable("userId") Long userId, @RequestBody UpdateBalanceRequest request);
}
