package ru.oxygensoftware.backoffice.ui;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.oxygensoftware.backoffice.service.ApplicationService;
import ru.oxygensoftware.backoffice.ui.view.ProductView;
import ru.oxygensoftware.backoffice.ui.view.UserView;
import ru.oxygensoftware.backoffice.ui.view.invite.InviteView;
import ru.oxygensoftware.backoffice.ui.view.systemuser.SystemUserView;

import javax.annotation.PostConstruct;

@Component
@Scope("prototype")
public class TreeNavigator extends Tree {
    @Autowired
    private ApplicationService app;

    @PostConstruct
    public void init() {
        setImmediate(true);
        setImmediate(true);
        addValueChangeListener(this::value);
        setWidth("250px");
        setHeight("100%");
        setNullSelectionAllowed(false);

        addNewItem(InviteView.NAME);
        addNewItem(ProductView.NAME);
        addNewItem(UserView.NAME);
        addNewItem(SystemUserView.NAME);
        addNewItem("Logout");
    }

    private void value(Property.ValueChangeEvent event) {
        Object itemId = event.getProperty().getValue();
        if (itemId == null) {
            return;
        } else if (itemId.equals("Logout")) {
            app.logout();
            return;
        }
        app.navigateTo((String) itemId);
    }

    private Item addNewItem(String viewName) {
        Item item = addItem(viewName);
        setItemCaption(viewName, viewName);
        setChildrenAllowed(viewName, false);
        setParent(viewName, null);
        return item;
    }
}
