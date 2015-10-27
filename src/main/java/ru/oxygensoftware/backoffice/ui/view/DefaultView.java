package ru.oxygensoftware.backoffice.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = DefaultView.NAME)
public class DefaultView extends VerticalLayout implements View {
    public final static String NAME = "";

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
