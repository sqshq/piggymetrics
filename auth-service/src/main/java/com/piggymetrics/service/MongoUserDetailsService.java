package com.piggymetrics.service;

import com.piggymetrics.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MongoUserDetailsService implements UserDetailsService {

//	@Autowired
//	UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//		User user = repository.findByUsername(username);
//
//		if (user == null) {
//			throw new UsernameNotFoundException(username);
//		}

		User user = new User();
		user.setUsername("sqshq");
		user.setPassword("password");

		return user;
	}
}
