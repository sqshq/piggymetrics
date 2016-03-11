package com.piggymetrics.notification.domain;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class NotificationSettings {

	@NotNull
	private Boolean active;

	@NotNull
	private Frequency frequency;

	private Date lastNotified;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Frequency getFrequency() {
		return frequency;
	}

	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	public Date getLastNotified() {
		return lastNotified;
	}

	public void setLastNotified(Date lastNotified) {
		this.lastNotified = lastNotified;
	}
}
