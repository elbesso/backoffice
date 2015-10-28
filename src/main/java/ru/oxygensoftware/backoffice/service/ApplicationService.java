package ru.oxygensoftware.backoffice.service;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ApplicationService {
    public Navigator getNavigator() {
        return UI.getCurrent().getNavigator();
    }

    public Button.ClickListener clickNavigator(String view) {
        return (any) -> navigateTo(view);
    }

    public void navigateTo(String view) {
        if (view == null) {
            return;
        }
        getNavigator().navigateTo(view);
    }

    public UserDetails getUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (obj instanceof UserDetails) {
            return (UserDetails) obj;
        } else {
            return new User(obj.toString(), obj.toString(), new ArrayList<>());
        }
    }

    public void logout() {
        UI.getCurrent().getSession().close();
        UI.getCurrent().getPage().setLocation("/logout");
    }
}
