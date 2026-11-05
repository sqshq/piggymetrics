package com.piggymetrics.account.repository;

import com.piggymetrics.account.domain.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

	Account findByName(String name);

}
