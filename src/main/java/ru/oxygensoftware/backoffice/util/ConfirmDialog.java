package ru.oxygensoftware.backoffice.util;

import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

/**
 * Created by Dmitry Raguzin
 * Date: 19.11.15
 */
public class ConfirmDialog extends Window {
    private Button cancel;

    public ConfirmDialog(String text, Button.ClickListener listener) {
        super("Warning!");
        center();
        setModal(true);
        setResizable(false);
        buildContent(listener, text);
    }

    private void buildContent(Button.ClickListener listener, String text) {
        HorizontalLayout buttonsHolder = new HorizontalLayout();
//        buttonsHolder.setMargin(true);
        buttonsHolder.setSpacing(true);
        buttonsHolder.setWidth("100%");
        buttonsHolder.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        buttonsHolder.addComponent(new Button("Yes", event -> {
            listener.buttonClick(event);
            close();
        }));
        cancel = new Button("No", event -> close());
        buttonsHolder.addComponent(cancel);
        Label label = new Label(text);
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSizeUndefined();
        content.addComponents(label, buttonsHolder);
        content.setSpacing(true);
        setContent(content);
    }

    public void addCancelListener(Button.ClickListener listener) {
        cancel.addClickListener(listener);
    }
}
