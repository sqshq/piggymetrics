package com.piggymetrics.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "statistics-service")
public interface StatisticsClient {

	@RequestMapping(method = RequestMethod.GET, value = "/statistics")
	String fire();

}
