package ru.oxygensoftware.backoffice.ui.view.systemuser;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.oxygensoftware.backoffice.data.SystemUser;
import ru.oxygensoftware.backoffice.service.SystemUserService;
import ru.oxygensoftware.backoffice.util.Constant;
import ru.oxygensoftware.backoffice.util.MyFieldGroupFieldFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SpringView(name = SystemUserView.NAME)
public class SystemUserView extends VerticalLayout implements View {
    public final static String NAME = "System User";

    private BeanItemContainer<SystemUser> container = new BeanItemContainer<>(SystemUser.class);
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private SystemUserService service;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        container.addAll(service.getAll());
        Table table = new Table();
        table.setContainerDataSource(container);
        table.setVisibleColumns("id", "username");
        table.setColumnHeaders("ID", "System User Name");
        table.setSizeFull();
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.addItemClickListener(event -> {
            if (event.isDoubleClick()) {
                if (event.getItem() != null) {
                    BeanItem<SystemUser> item = (BeanItem<SystemUser>) event.getItem();
                    openWindow(new EditSystemUserWindow(item.getBean()));
                }
            }
        });

        Button add = new Button();
        add.addClickListener(event -> openWindow(new EditSystemUserWindow()));
        add.setCaption("Add");

        Button delete = new Button("Delete", event -> {
            Object value = table.getValue();
            if (value != null) {
                ((Collection<SystemUser>) value).stream().forEach(item -> service.delete(item.getId()));
                refresh();
                table.setValue(null);
            }
        });

        Button edit = new Button("Edit", event -> {
            if (table.getValue() != null) {
                Set<SystemUser> systemUsers = (Set<SystemUser>) table.getValue();
                if (systemUsers.size() == 1) {
                    List<SystemUser> list = new ArrayList<>(systemUsers);
                    openWindow(new EditSystemUserWindow(list.get(0)));
                }
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(add, edit, delete);
        buttonLayout.setSizeUndefined();
        buttonLayout.setSpacing(true);

        addComponents(buttonLayout, table);
        setSizeFull();
        setMargin(true);
        setSpacing(true);
        setExpandRatio(table, 1.0f);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private void refresh() {
        container.removeAllItems();
        container.addAll(service.getAll());
    }

    private void openWindow(Window window) {
        UI.getCurrent().addWindow(window);
    }

    private class EditSystemUserWindow extends Window {
        private PasswordField newPassword;
        private PasswordField oldPassword;

        public EditSystemUserWindow() {
            build(null);
            setCaption("Add System User");
        }

        public EditSystemUserWindow(SystemUser systemUser) {
            build(systemUser);
            setCaption("Edit System User");
        }

        private void build(SystemUser systemUser) {
            final boolean isCreate = systemUser == null;
            BeanFieldGroup<SystemUser> fieldGroup = new BeanFieldGroup<>(SystemUser.class);
            fieldGroup.setFieldFactory(new MyFieldGroupFieldFactory());
            if (systemUser == null) {
                fieldGroup.setItemDataSource(service.create());
            } else {
                fieldGroup.setItemDataSource(systemUser);
            }

            PasswordField confirmPassword = new PasswordField("Confirm password");
            confirmPassword.setNullRepresentation("");
            confirmPassword.setVisible(false);
            confirmPassword.setImmediate(true);
            confirmPassword.setWidth(Constant.STANDARD_FIELD_WIDTH);
            confirmPassword.addValidator(value -> {
                if (!newPassword.getValue().equals("") && !value.equals(newPassword.getValue())) {
                    throw new Validator.InvalidValueException("Passwords don't match");
                }
            });

            oldPassword = new PasswordField("Old password");
            oldPassword.setNullRepresentation("");
            oldPassword.setVisible(false);
            oldPassword.setImmediate(true);
            oldPassword.setWidth(Constant.STANDARD_FIELD_WIDTH);
            oldPassword.addValidator(value -> {
                if (!newPassword.getValue().equals("") && !isCreate) {
                    if (!encoder.matches(oldPassword.getValue(), systemUser.getPassword())) {
                        throw new Validator.InvalidValueException("Wrong password!");
                    }
                }
            });

            TextField name = fieldGroup.buildAndBind("System User Name", "username", TextField.class);
            name.setNullRepresentation("");
            name.setRequiredError("System user's name should not be null");
            name.setRequired(true);
            name.addValidator(new StringLengthValidator("Filed length must be less than or equal to 255", 1, 255, false));

            newPassword = new PasswordField("New Password");
            newPassword.setNullRepresentation("");
            newPassword.setWidth(Constant.STANDARD_FIELD_WIDTH);
            newPassword.addValidator(value -> {
                String v = (String) value;
                if (v != null && !v.equals("")) {
                    if ((v.length() < 6) || (v.length() > 255)) {
                        throw new Validator.InvalidValueException("Filed length must be greater than or equal to 6 and " +
                                "less than or equal to 255");
                    }
                }
            });
            newPassword.setImmediate(true);
            if (systemUser == null) {
                newPassword.setRequired(true);
            }
            newPassword.addValueChangeListener(event -> {
                if (((String) event.getProperty().getValue()).length() > 0) {
                    confirmPassword.setVisible(true);
                    confirmPassword.setRequired(true);
                    if (!isCreate) {
                        oldPassword.setVisible(true);
                        oldPassword.setRequired(true);
                    }
                } else {
                    confirmPassword.setVisible(false);
                    confirmPassword.setRequired(false);
                    oldPassword.setVisible(false);
                    oldPassword.setRequired(false);
                    oldPassword.setValue(null);
                    confirmPassword.setValue(null);
                }
            });

            Button save = new Button("Save", event -> {
                try {
                    oldPassword.validate();
                    newPassword.validate();
                    confirmPassword.validate();
                    fieldGroup.commit();
                    if (newPassword.getValue().length() > 0) {
                        fieldGroup.getItemDataSource().getBean().setPassword(newPassword.getValue());
                        service.save(fieldGroup.getItemDataSource().getBean(), true);
                    } else {
                        service.save(fieldGroup.getItemDataSource().getBean(), false);
                    }
                    this.close();
                    refresh();
                } catch (Validator.InvalidValueException ive) {
                    Notification.show("Some fields don't meet requirements");
                } catch (FieldGroup.CommitException e) {
                    Notification.show(e.getMessage());
                }
            });
            save.setWidthUndefined();

            FormLayout layout = new FormLayout();
            layout.setSizeUndefined();
            layout.addComponents(name, newPassword, confirmPassword, oldPassword, save);
            layout.setComponentAlignment(save, Alignment.MIDDLE_CENTER);
            layout.setMargin(true);
            layout.setSpacing(true);

            setContent(layout);
            center();
            setModal(true);
            setResizable(false);
        }
    }
}
