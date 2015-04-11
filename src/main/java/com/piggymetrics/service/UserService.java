package com.piggymetrics.service;

import com.piggymetrics.dao.interfaces.UserDao;
import com.piggymetrics.domain.User;
import com.piggymetrics.helpers.LangMessage;
import com.piggymetrics.service.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private LangMessage lang;

    @Transactional
    public User getUser(String username, HttpServletRequest request) {

        User user = userDao.select(username);

        if (user.getUserpic() == null) {
            user.setUserpic(lang.get("userpic", request));
        }

        return user;
    }

    @Transactional
    public User getDemoUser(HttpServletRequest request) {

        User user = userDao.select(lang.get("demo", request));
        user.setUsername("Demo");

        return user;
    }

    @Transactional
    public void saveChanges(User user, HttpServletRequest request) {
        userDao.update(user, request.getRemoteAddr(), request.getLocale().getLanguage());
    }

    @Transactional
    public void saveEmail(User user) {
        userDao.saveEmail(user);
    }

    @Transactional
    public void addUser(User user, HttpServletRequest request) {

        String password = user.getPassword();

        StandardPasswordEncoder encoder = new StandardPasswordEncoder();
        user.setPassword(encoder.encode(password));
        userDao.insertUser(user, request.getRemoteAddr(), request.getLocale().getLanguage());

        authUser(user.getUsername(), password, request);
    }

    private void authUser(String username, String password, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        request.getSession();

        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticatedUser = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
    }
}
