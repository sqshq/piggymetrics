package com.piggymetrics.dao;

import com.piggymetrics.dao.interfaces.QuotesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcQuotesDao extends JdbcDaoSupport implements QuotesDao {

    @Autowired
    JdbcQuotesDao(DataSource dataSource){
        setJdbcTemplate(new JdbcTemplate(dataSource));
        setDataSource(dataSource);
    }

    @Override
    public void update(Double usd, Double eur) {
        String sql = "UPDATE settings SET usd = ?, eur = ?";
        getJdbcTemplate().update(sql, usd, eur);
    }
}