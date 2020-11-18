package com.piggymetrics.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;

/**
 * 断路器集权监控。
 * Turbine能够汇集监控信息，并将聚合后的信息提供给Hystrix Dashboard来集中展示和监控。 @EnableTurbine
 *
 * @EnableTurbineStream 封装了基于消息代理的收集实现
 * 我们可以将所有需要收集的监控信息都输出到消息代理中，然后Turbine服务再从消息代理中异步获取这些监控信息，最后将这些监控信息聚合并输出到Hystrix Dashboard中
 */
@SpringBootApplication
@EnableTurbineStream
@EnableDiscoveryClient
public class TurbineStreamServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurbineStreamServiceApplication.class, args);
	}
}
