package com.piggymetrics.service.interfaces;

import com.piggymetrics.domain.User;

import javax.servlet.http.HttpServletRequest;

public interface UserServiceInterface {

    public com.piggymetrics.domain.User getUser(String username, HttpServletRequest request);

    public com.piggymetrics.domain.User getDemoUser(HttpServletRequest request);

    public void saveChanges(com.piggymetrics.domain.User user, HttpServletRequest request);

    public void addUser(com.piggymetrics.domain.User user, HttpServletRequest request);
}
