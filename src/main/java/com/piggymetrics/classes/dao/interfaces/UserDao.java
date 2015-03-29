package com.piggymetrics.classes.dao.interfaces;

public interface UserDao {

    public void select(String username);

    public void insert();

    public void update();

    public void updateLastVisit();

}
