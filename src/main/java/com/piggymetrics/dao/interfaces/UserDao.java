package com.piggymetrics.dao.interfaces;

import com.piggymetrics.domain.User;
import java.util.List;

public interface UserDao {

    public User insertUser(User user, String IP, String language);

    public User update(User user, String IP, String language);

    public User select(String username);

    public List<User> selectForBackup();

    public void saveEmail(User user);

}
