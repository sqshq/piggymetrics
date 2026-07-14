package com.piggymetrics.account.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "savings")
public class Saving {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; // Added primary key for relational H2 mapping

	@NotNull
	private BigDecimal amount;

	@NotNull
	@Enumerated(EnumType.STRING) // Tells Hibernate to save enum values as text strings
	private Currency currency;

	@NotNull
	private BigDecimal interest;

	@NotNull
	private Boolean deposit;

	@NotNull
	private Boolean capitalization;

	// Added Getter and Setter for the primary key
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public Boolean getDeposit() {
		return deposit;
	}

	public void setDeposit(Boolean deposit) {
		this.deposit = deposit;
	}

	public Boolean getCapitalization() {
		return capitalization;
	}

	public void setCapitalization(Boolean capitalization) {
		this.capitalization = capitalization;
	}
}