package com.piggymetrics.statistics.domain;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "accounts")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

	private ObjectId accountId;

	private String name;

	private List<Item> incomes;

	private List<Item> expenses;

	private Saving saving;

	public ObjectId getAccountId() {
		return accountId;
	}

	public void setAccountId(ObjectId accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Item> getIncomes() {
		return incomes;
	}

	public void setIncomes(List<Item> incomes) {
		this.incomes = incomes;
	}

	public List<Item> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Item> expenses) {
		this.expenses = expenses;
	}

	public Saving getSaving() {
		return saving;
	}

	public void setSaving(Saving saving) {
		this.saving = saving;
	}
}
