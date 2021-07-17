package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author cdov
 */
@Component
@Slf4j
public class StatisticsServiceClientFallback implements StatisticsServiceClient {
    @Override
    public void updateStatistics(String accountName, Account account) {
        log.error("Error during update statistics for account: {}", accountName);
    }
}
