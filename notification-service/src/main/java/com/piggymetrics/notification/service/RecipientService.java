package com.piggymetrics.notification.service;

import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;

import java.util.List;

public interface RecipientService {

	Object getByAccountName(String accountName);

	List<Recipient> findReadyToNotify(NotificationType type);

	Recipient save(String accountName, Recipient recipient);

	void markNotified(NotificationType type, Recipient recipient);
}
