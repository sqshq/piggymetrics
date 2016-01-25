package com.piggymetrics.repository;

import com.piggymetrics.domain.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

	//Account findByUserUsername(String username);

}
