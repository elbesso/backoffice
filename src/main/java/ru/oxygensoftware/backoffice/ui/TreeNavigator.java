package ru.oxygensoftware.backoffice.ui;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.Resource;
import com.vaadin.ui.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.oxygensoftware.backoffice.service.ApplicationService;
import ru.oxygensoftware.backoffice.ui.view.ProductView;
import ru.oxygensoftware.backoffice.ui.view.UserView;
import ru.oxygensoftware.backoffice.ui.view.invite.InviteTableView;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class TreeNavigator extends Tree {
    @Autowired
    private ApplicationService app;
    private Map<String, String> viewMap = new HashMap<>();

    @PostConstruct
    public void init() {
        setImmediate(true);
        setImmediate(true);
        addValueChangeListener(this::value);
        setWidth("250px");
        setHeight("100%");
        setNullSelectionAllowed(false);

        addItem("Table", null, null, InviteTableView.NAME, false);
        addItem("Product", null, null, ProductView.NAME, false);
        addItem("User", null, null, UserView.NAME, false);
        addItem("Logout", null, null, null, false);
    }

    private void value(Property.ValueChangeEvent event) {
        Object itemId = event.getProperty().getValue();
        if (itemId == null) {
            return;
        } else if (itemId.equals("Logout")) {
            app.logout();
        }
        String viewName = viewMap.get(itemId);
        if (viewName != null && !viewName.isEmpty()) {
            app.navigateTo(viewName);
        }
    }

    private Item addItem(String message, String parentId, Resource icon, String viewName, boolean isChildrenAllowed) {
        Item item = addItem(message);
        setItemCaption(message, message);
        setItemIcon(message, icon);
        setChildrenAllowed(message, isChildrenAllowed);
        setParent(message, parentId);
        if (!message.equals("Logout")) {
            viewMap.put(message, viewName);
        }
        return item;
    }
}
