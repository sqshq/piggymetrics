package com.piggymetrics.service;

import com.piggymetrics.dao.interfaces.QuotesDao;
import com.piggymetrics.service.interfaces.QuotesServiceInterface;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class QuotesService implements QuotesServiceInterface {

    @Value("${quotes.link}")
    private String quotesLink;

    @Autowired
    private QuotesDao quotesDao;

    static final Logger logger = Logger.getLogger(QuotesService.class);

    private final String usdId = "R01235";
    private final String eurId = "R01239";

    @Scheduled(cron="${quotes.schedule}")
    public void parseQuotes() {

        NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
        XPath xPath =  XPathFactory.newInstance().newXPath();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xml = builder.parse(new URL(quotesLink).openStream());

            Double usd = format.parse(xPath.compile("/ValCurs/Valute[@ID='" + usdId + "']/Value").evaluate(xml)).doubleValue();
            Double eur = format.parse(xPath.compile("/ValCurs/Valute[@ID='" + eurId + "']/Value").evaluate(xml)).doubleValue();

            logger.info(String.format("USD: %f, EUR: %f", usd, eur));

            quotesDao.update(usd, eur);

        } catch (Exception e) {
            logger.error("Quotes parsing failed", e);
        }
    }
}