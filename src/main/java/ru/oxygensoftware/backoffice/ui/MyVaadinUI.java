package ru.oxygensoftware.backoffice.ui;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import ru.oxygensoftware.backoffice.data.Invite;
import ru.oxygensoftware.backoffice.service.InviteService;

@SpringUI
public class MyVaadinUI extends UI {
    @Autowired
    private InviteService service;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Layout layout = new VerticalLayout();
        layout.setSizeFull();

        Table table = new Table();
        table.setSizeFull();

        BeanItemContainer<Invite> container = new BeanItemContainer<>(Invite.class);
        table.setContainerDataSource(container);

        layout.addComponent(table);
        setContent(layout);
    }
}
