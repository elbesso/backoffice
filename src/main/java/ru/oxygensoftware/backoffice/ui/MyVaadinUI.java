package ru.oxygensoftware.backoffice.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import ru.oxygensoftware.backoffice.data.Invite;
import ru.oxygensoftware.backoffice.service.InviteService;

@SpringUI
public class MyVaadinUI extends UI {
    @Autowired
    private InviteService service;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Invite invite = service.create();
        service.save(invite);

        setContent(new Label(service.getAll().get(0).getComment()));
    }
}
