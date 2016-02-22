package com.piggymetrics.auth.service.security;

import com.piggymetrics.auth.domain.Client;
import com.piggymetrics.auth.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

@Service
public class MongoClientDetailsService implements ClientDetailsService {

	@Autowired
	private ClientRepository repository;

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

		Client client = repository.findOne(clientId);

		if (client == null) {
			throw new IllegalArgumentException("client doesn't exist: " + clientId);
		}

		return client;
	}
}
