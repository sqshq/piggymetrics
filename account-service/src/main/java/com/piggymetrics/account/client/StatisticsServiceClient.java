package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@FeignClient(name = "statistics-service", fallback = StatisticsServiceClientFallback.class)
public interface StatisticsServiceClient {

  @RequestMapping(
      method = RequestMethod.PUT,
      value = "/statistics/{accountName}",
      consumes = APPLICATION_JSON_UTF8_VALUE)
  void updateStatistics(@PathVariable("accountName") String accountName, Account account);
}
