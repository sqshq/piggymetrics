package com.piggymetrics.notification.service;

import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmailServiceImplTest {

	@InjectMocks
	private EmailServiceImpl emailService;

	@Mock
	private JavaMailSender mailSender;

	@Mock
	private Environment env;

	@Captor
	private ArgumentCaptor<MimeMessage> captor;

	@Before
	public void setup() {
		initMocks(this);
		when(mailSender.createMimeMessage())
				.thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));
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
		// TODO check other fields
	}
}