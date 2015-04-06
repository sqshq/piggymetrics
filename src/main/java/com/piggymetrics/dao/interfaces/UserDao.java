package com.piggymetrics.dao.interfaces;

import com.piggymetrics.model.User;

public interface UserDao {

    public void insertUser(User user);

    public void saveEmail(String username, User user);

    public User select(String username);

    public void update(String username, User user);

    public void updateVisit(String username, String IP, String language);

}
