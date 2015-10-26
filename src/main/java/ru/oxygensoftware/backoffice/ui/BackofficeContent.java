package ru.oxygensoftware.backoffice.ui;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("prototype")
public class BackofficeContent extends VerticalLayout {
    @Autowired
    private TreeNavigator treeNavigator;

    @PostConstruct
    public void init() {
        setSizeFull();
        addComponent(new Label("Oxygen Software"));
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeUndefined();
        layout.addComponent(treeNavigator);
        addComponent(layout);
    }
}
