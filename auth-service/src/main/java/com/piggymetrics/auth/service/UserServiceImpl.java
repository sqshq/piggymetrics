package com.piggymetrics.auth.service;

import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repository;

	@Override
	public void create(User user) {

		User existing = repository.findByUsername(user.getUsername());
		Assert.isNull(existing, "user already exists: " + user.getUsername());

		// TODO password encoder
		repository.save(user);
	}
}
