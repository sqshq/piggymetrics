package com.piggymetrics.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "accounts")
public class Account {

	private ObjectId accountId;

	private List<Item> incomes;

	private List<Item> expenses;

	private Saving saving;

}
