package com.piggymetrics.notification.service;

import com.piggymetrics.notification.client.AccountServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

	public static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

	@Autowired
	private AccountServiceClient client;

	@Scheduled(fixedDelayString = "${delay.value}")
	public void notifyUsers() {
		log.info(client.getAccount("demo"));
	}
}
