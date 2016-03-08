package com.piggymetrics.notification.domain;

public enum NotificationType {

	BACKUP("backup.email.subject", "backup.email.text", "backup.email.attachment"),
	REMIND("remind.email.subject", "remind.email.text", null);

	private String subject;
	private String text;
	private String attachment;

	NotificationType(String subject, String text, String attachment) {
		this.subject = subject;
		this.text = text;
		this.attachment = attachment;
	}

	public String getSubject() {
		return subject;
	}

	public String getText() {
		return text;
	}

	public String getAttachment() {
		return attachment;
	}
}
