package ru.oxygensoftware.backoffice.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = SystemUserView.NAME)
public class SystemUserView extends VerticalLayout implements View {
    public final static String NAME = "SystemUser";

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
