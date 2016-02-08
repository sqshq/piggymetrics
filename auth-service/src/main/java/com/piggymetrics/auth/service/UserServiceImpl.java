package com.piggymetrics.auth.service;

import com.piggymetrics.auth.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

//	@Autowired
//	private UserRepository repository;

	@Override
	public void create(User user) {
		// TODO check existance
//		repository.save(user);
	}
}
