package com.piggymetrics.notification.repository;

import com.piggymetrics.notification.domain.NotificationSettings;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipientRepository extends CrudRepository<Recipient, String> {

	/** Shortest supported frequency; the database pre-filters, Java applies each recipient's frequency. */
	int MIN_FREQUENCY_DAYS = 7;

	Recipient findByAccountName(String name);

	// Replaces the legacy $where JavaScript query, which managed MongoDB services restrict or disable.
	@Query("{'scheduledNotifications.BACKUP.active': true, 'scheduledNotifications.BACKUP.lastNotified': {$lt: ?0}}")
	List<Recipient> findBackupCandidates(Date notifiedBefore);

	@Query("{'scheduledNotifications.REMIND.active': true, 'scheduledNotifications.REMIND.lastNotified': {$lt: ?0}}")
	List<Recipient> findRemindCandidates(Date notifiedBefore);

	default List<Recipient> findReadyForBackup() {
		return due(NotificationType.BACKUP, findBackupCandidates(cutoff(MIN_FREQUENCY_DAYS)));
	}

	default List<Recipient> findReadyForRemind() {
		return due(NotificationType.REMIND, findRemindCandidates(cutoff(MIN_FREQUENCY_DAYS)));
	}

	private static List<Recipient> due(NotificationType type, List<Recipient> candidates) {
		return candidates.stream().filter(recipient -> {
			NotificationSettings settings = recipient.getScheduledNotifications().get(type);
			return settings.getLastNotified().before(cutoff(settings.getFrequency().getDays()));
		}).toList();
	}

	private static Date cutoff(int days) {
		return Date.from(Instant.now().minus(days, ChronoUnit.DAYS));
	}
}
