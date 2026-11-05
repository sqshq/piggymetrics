package com.piggymetrics.notification.service;

import com.piggymetrics.notification.client.AccountServiceClient;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private AccountServiceClient client;
    @Autowired
    private RecipientService recipientService;
    @Autowired
    private EmailService emailService;

    @Override
    public void sendBackupNotifications() {
        send(NotificationType.BACKUP);
    }

    @Override
    public void sendRemindNotifications() {
        send(NotificationType.REMIND);
    }

    private void send(NotificationType type) {
        int failures = 0;
        // Complete work before returning, Lambda can freeze background threads immediately.
        for (Recipient recipient : recipientService.findReadyToNotify(type)) {
            try {
                String attachment = type == NotificationType.BACKUP
                        ? client.getAccount(recipient.getAccountName()) : null;
                emailService.send(type, recipient, attachment);
                recipientService.markNotified(type, recipient);
            } catch (Exception exception) {
                failures++;
                log.error("Notification delivery failed ({})", exception.getClass().getSimpleName());
            }
        }
        if (failures > 0) {
            throw new IllegalStateException("Some notifications failed; retry pending deliveries");
        }
    }
}
