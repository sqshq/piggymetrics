package com.piggymetrics.notification.service;

import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class EmailServiceImplTest {

	@InjectMocks
	private EmailServiceImpl emailService;

	@Mock
	private JavaMailSender mailSender;

	@Mock
	private Environment env;

	@Captor
	private ArgumentCaptor<MimeMessage> captor;

	@BeforeEach
	public void setup() {
		openMocks(this);
		when(mailSender.createMimeMessage())
				.thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));
		when(env.getRequiredProperty("notifications.from")).thenReturn("sender@example.invalid");
	}

	@Test
	public void shouldSendBackupEmail() throws MessagingException, IOException {

		final String subject = "subject";
		final String text = "text";
		final String attachment = "attachment.json";

		Recipient recipient = new Recipient();
		recipient.setAccountName("test");
		recipient.setEmail("test@test.com");

		when(env.getProperty(NotificationType.BACKUP.getSubject())).thenReturn(subject);
		when(env.getProperty(NotificationType.BACKUP.getText())).thenReturn(text);
		when(env.getProperty(NotificationType.BACKUP.getAttachment())).thenReturn(attachment);

		emailService.send(NotificationType.BACKUP, recipient, "{\"name\":\"test\"");

		verify(mailSender).send(captor.capture());

		MimeMessage message = captor.getValue();
		assertEquals(subject, message.getSubject());
		assertEquals("sender@example.invalid", message.getFrom()[0].toString());
		// TODO check other fields
	}

	@Test
	public void shouldSendRemindEmail() throws MessagingException, IOException {

		final String subject = "subject";
		final String text = "text";

		Recipient recipient = new Recipient();
		recipient.setAccountName("test");
		recipient.setEmail("test@test.com");

		when(env.getProperty(NotificationType.REMIND.getSubject())).thenReturn(subject);
		when(env.getProperty(NotificationType.REMIND.getText())).thenReturn(text);

		emailService.send(NotificationType.REMIND, recipient, null);

		verify(mailSender).send(captor.capture());

		MimeMessage message = captor.getValue();
		assertEquals(subject, message.getSubject());
		assertEquals("sender@example.invalid", message.getFrom()[0].toString());
		// TODO check other fields
	}
}