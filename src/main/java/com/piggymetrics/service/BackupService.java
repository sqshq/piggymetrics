package com.piggymetrics.service;

import com.piggymetrics.dao.interfaces.QuotesDao;
import com.piggymetrics.service.interfaces.BackupServiceInterface;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Service
public class BackupService implements BackupServiceInterface {

    static final Logger logger = Logger.getLogger(BackupService.class);

    @Scheduled(cron="${backup.schedule}")
    public void backupUsersData() {
        // Пробегаем по всем юзерам которым нужно сделать бекап
        // Формируем письмо с данными
        // Шлем к хренам на почту и отчитываемся в лог инфо о старте, проблемах и окончании
    }

}