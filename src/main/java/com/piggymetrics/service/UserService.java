package com.piggymetrics.service;

import com.piggymetrics.dao.interfaces.UserDao;
import com.piggymetrics.model.User;
import com.piggymetrics.service.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Service
public class UserService implements UserServiceInterface, MessageSourceAware { // @todo обовить интерфейс

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDao userDao;

    private Locale locale = LocaleContextHolder.getLocale();
    private MessageSource messageSource;

    @Transactional
    public User getUser(String username, HttpServletRequest request) {
        System.out.println(request.getLocale().getLanguage()); // @todo отдавть в апдейт визит
        User user = userDao.select(username);
        userDao.updateVisit(username, request.getRemoteAddr());

        if (user.getUserpic() == null) {
            user.setUserpic(messageSource.getMessage("userpic", null, locale));
        }

        return user;
    }

    @Transactional
    public User getDemoUser() {

        User user = userDao.select(messageSource.getMessage("demo", null, locale));

        user.setUsername("Demo");

        return user;
    }

    @Transactional
    public void saveChanges(String username, User user) {
        userDao.update(username, user);
    }

    @Transactional
    public void addUser(User user, HttpServletRequest request) {
        userDao.insertUser(user);
        authUser(user, request);
    }

    private void authUser(User user, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        request.getSession();

        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticatedUser = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
    }
}
