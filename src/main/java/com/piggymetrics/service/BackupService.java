package com.piggymetrics.service;

import com.piggymetrics.dao.interfaces.UserDao;
import com.piggymetrics.domain.User;
import com.piggymetrics.helpers.JsonMapper;
import com.piggymetrics.helpers.LangMessage;
import com.piggymetrics.service.interfaces.BackupServiceInterface;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Service
public class BackupService implements BackupServiceInterface {

    @Autowired
    private UserDao userDao;

    @Autowired
    private LangMessage lang;

    @Autowired
    private JsonMapper mapper;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    static final Logger logger = Logger.getLogger(BackupService.class);

    @Scheduled(cron="${backup.schedule}")
    public void backupUsersData() {
        System.out.println("backup try");
        try {
            List<User> users = userDao.selectForBackup();
            for (User user : users) {
                sendEmail(user);
            }
        } catch (Exception e) {
            logger.error("Backup failed", e);
        }
    }

    private void sendEmail(User user) throws MessagingException, IOException {

        String text = prepareText(user);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("noreply@piggymetrics.com", "PiggyMetrics"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
        message.setSubject(lang.get("backup.subject", user.getLanguage()));
        message.setText(text, "UTF-8");

        Transport.send(message);
    }

    private String prepareText(User user) throws IOException {

        StringBuilder text = new StringBuilder();
        String language = user.getLanguage();

        String on  = lang.get("backup.on", language);
        String off = lang.get("backup.off", language);

        text.append(lang.get("backup.howdy", language));
        text.append(user.getUsername());

        text.append(lang.get("backup.ready", language));
        text.append(lang.get("backup.money", language));
        text.append(user.getMoney() + " " + user.getCheckedCurrency());

        text.append(lang.get("backup.deposit", language));
        text.append(user.isDeposit() ? on : off);

        text.append(lang.get("backup.c12n", language));
        text.append(user.isCapitalization() ? on : off);

        text.append(lang.get("backup.interest", language));
        text.append(user.isDeposit() ? user.getInterest() + "%" : "-");

        text.append(lang.get("backup.notes", language));
        text.append(user.getNote());

        text.append(lang.get("backup.data", language));

        Object json = mapper.readValue(user.getData(), Object.class);
        String data = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        text.append(data);

        return text.toString();
    }
}