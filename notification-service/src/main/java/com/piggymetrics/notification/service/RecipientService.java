package com.piggymetrics.notification.service;

import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;

import java.util.List;

public interface RecipientService {

	/**
	 * Finds recipient by account name
	 *
	 * @param accountName
	 * @return recipient
	 */
	Recipient findByAccountName(String accountName);

	/**
	 * Finds recipients, which are ready to be notified
	 * at the moment
	 *
	 * @param type
	 * @return recipients to notify
	 */
	List<Recipient> findReadyToNotify(NotificationType type);

	/**
	 * Creates or updates recipient settings
	 *
	 * @param accountName
	 * @param recipient
	 * @return updated recipient
	 */
	Recipient save(String accountName, Recipient recipient);

	/**
	 * Updates {@link NotificationType} {@code lastNotified} property with current date
	 * for given recipient.
	 *
	 * @param type
	 * @param recipient
	 */
	void markNotified(NotificationType type, Recipient recipient);
}
