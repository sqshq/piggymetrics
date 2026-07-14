package com.piggymetrics.account.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "accounts")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

	@Id
	private String name;

	private Date lastSeen;

	// Relational mappings for your lists
	@Valid
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Item> incomes;

	@Valid
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Item> expenses;

	// Relational mapping for a single object
	@Valid
	@NotNull
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Saving saving;

	@Length(min = 0, max = 20_000)
	private String note;

	// NEW FEATURE FIELDS ADDED HERE:
	private BigDecimal customSavingsGoalAmount;
	private String customSavingsGoalDescription;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	// GETTERS AND SETTERS FOR YOUR NEW FEATURE:
	public BigDecimal getCustomSavingsGoalAmount() {
		return customSavingsGoalAmount;
	}

	public void setCustomSavingsGoalAmount(BigDecimal customSavingsGoalAmount) {
		this.customSavingsGoalAmount = customSavingsGoalAmount;
	}

	public String getCustomSavingsGoalDescription() {
		return customSavingsGoalDescription;
	}

	public void setCustomSavingsGoalDescription(String customSavingsGoalDescription) {
		this.customSavingsGoalDescription = customSavingsGoalDescription;
	}
}
