package ru.oxygensoftware.backoffice.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI
@Theme("mytheme")
public class MyVaadinUI extends UI {
    @Autowired
    private SpringViewProvider viewProvider;
    @Autowired
    private TreeNavigator treeNavigator;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.addComponent(treeNavigator);

        final Panel viewContainer = new Panel();
        viewContainer.setSizeFull();
        hl.addComponent(viewContainer);
        hl.setExpandRatio(viewContainer, 1.0f);

        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);
        setContent(root);
        Label title = new Label("Oxygen Software");
        title.setWidthUndefined();
        root.addComponent(title);
        root.addComponent(new Label("<hr/>", ContentMode.HTML));
        root.addComponent(hl);
        root.setExpandRatio(hl, 1.0f);
        root.setComponentAlignment(title, Alignment.MIDDLE_CENTER);

        Navigator navigator = new Navigator(this, viewContainer);
        navigator.addProvider(viewProvider);
    }
}