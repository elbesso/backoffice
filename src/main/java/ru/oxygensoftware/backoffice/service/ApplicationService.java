package ru.oxygensoftware.backoffice.service;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.oxygensoftware.backoffice.data.SystemUser;
import ru.oxygensoftware.backoffice.ui.view.ViewName;

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
            getNavigator().navigateTo(ViewName.MAIN);
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

    public SystemUser getSystemUser() {
        UserDetails details = getUser();
        if (details instanceof SystemUser) {
            return (SystemUser) details;
        }
        return null;
    }

    public void login() {
        UI.getCurrent().getPage().setLocation("/spring_security_login");
    }

    public <T> void login(T unused) {
        login();
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation("/j_spring_security_logout");
    }

    public void reload() {
        UI.getCurrent().getPage().reload();
    }

    public <T> void logout(T unused) {
        logout();
    }
}
