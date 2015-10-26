package ru.oxygensoftware.backoffice.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import ru.oxygensoftware.backoffice.data.Invite;
import ru.oxygensoftware.backoffice.data.Product;
import ru.oxygensoftware.backoffice.service.InviteService;
import ru.oxygensoftware.backoffice.service.ProductService;

import java.util.Date;

@SpringUI
@Theme("mytheme")
public class MyVaadinUI extends UI {
    @Autowired
    private BackofficeContent content;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setContent(content);
    }
}
