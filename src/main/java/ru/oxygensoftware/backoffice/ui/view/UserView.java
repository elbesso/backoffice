package ru.oxygensoftware.backoffice.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = UserView.NAME)
public class UserView extends VerticalLayout implements View {
    public final static String NAME = "User";

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
