package com.piggymetrics.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;

@SpringBootApplication
@EnableTurbineStream
@EnableDiscoveryClient
public class TurbineStreamServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurbineStreamServiceApplication.class, args);
	}
}
