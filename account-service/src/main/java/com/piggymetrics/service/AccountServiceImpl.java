package com.piggymetrics.service;

import com.piggymetrics.client.StatisticsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private StatisticsClient client;

	public void fire() {
		System.out.println(client.fire());
	}
}
