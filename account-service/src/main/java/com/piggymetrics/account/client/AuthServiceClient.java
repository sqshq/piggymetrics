package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

	@RequestMapping(method = RequestMethod.POST, value = "/uaa/users", consumes = APPLICATION_JSON_UTF8_VALUE)
	void createUser(User user);

}
