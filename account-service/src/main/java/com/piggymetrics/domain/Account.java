package com.piggymetrics.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
public class Account {

	@Id
	private ObjectId accountId;

	public ObjectId getAccountId() {
		return accountId;
	}

	public void setAccountId(ObjectId accountId) {
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		return "Account{" +
				"accountId=" + accountId +
				'}';
	}
}
