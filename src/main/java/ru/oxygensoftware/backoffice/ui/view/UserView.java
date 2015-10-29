package ru.oxygensoftware.backoffice.ui.view;

import com.vaadin.data.Validatable;
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
import org.vaadin.hene.expandingtextarea.ExpandingTextArea;
import ru.oxygensoftware.backoffice.data.ISO_3166_CountryCode;
import ru.oxygensoftware.backoffice.data.Product;
import ru.oxygensoftware.backoffice.data.User;
import ru.oxygensoftware.backoffice.service.UserService;

import javax.annotation.PostConstruct;
import java.util.*;

@SpringView(name = UserView.NAME)
public class UserView extends VerticalLayout implements View {
    public final static String NAME = "User";
    private BeanItemContainer<User> container = new BeanItemContainer<>(User.class);
    @Autowired
    private UserService service;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        container.addAll(service.getAll());
        Table table = new Table();
        table.setContainerDataSource(container);
        table.setVisibleColumns("id", "name", "surname", "organization", "position", "email", "country", "state",
                "city", "postcode", "address", "phoneNumber");
        table.setColumnHeaders("ID", "Name", "Surname", "Organization", "Position", "Email", "Country", "State",
                "City", "Postcode", "Address", "Phone Number");
        table.setSizeFull();
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.addItemClickListener(event -> {
            if (event.isDoubleClick()) {
                if (event.getItem() != null) {
                    BeanItem<User> item = (BeanItem<User>) event.getItem();
                    openWindow(new EditUserWindow(item.getBean()));
                }
            }
        });

        Button add = new Button();
        add.addClickListener(event -> openWindow(new EditUserWindow()));
        add.setCaption("Add");

        Button delete = new Button("Delete", event -> {
            Object value = table.getValue();
            if (value != null) {
                ((Collection<User>) value).stream().forEach(item -> service.delete(item.getId()));
                refresh();
                table.setValue(null);
            }
        });

        Button edit = new Button("Edit", event -> {
            if (table.getValue() != null) {
                Set<User> products = (Set<User>) table.getValue();
                if (products.size() == 1) {
                    List<User> list = new ArrayList<>(products);
                    openWindow(new EditUserWindow(list.get(0)));
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

    private void refresh() {
        container.removeAllItems();
        container.addAll(service.getAll());
    }

    private void openWindow(Window window) {
        UI.getCurrent().addWindow(window);
    }

    private class EditUserWindow extends Window {
        private BeanFieldGroup<User> fieldGroup;
        public EditUserWindow() {
            build(null);
        }

        public EditUserWindow(User user) {
            build(user);
        }

        private void build(User user) {
            fieldGroup = new BeanFieldGroup<>(User.class);
            if (user == null) {
                fieldGroup.setItemDataSource(service.create());
            } else {
                fieldGroup.setItemDataSource(user);
            }

            ComboBox country = new ComboBox("Country");
            country.setContainerDataSource(new BeanItemContainer<>(ISO_3166_CountryCode.class, Arrays.asList(ISO_3166_CountryCode.values())));
            country.setItemCaptionPropertyId("countryName");
            country.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            country.setNullSelectionAllowed(false);
            country.setRequired(true);
            country.setImmediate(true);
            country.setRequiredError("Select a country");
            fieldGroup.bind(country, "country");

            TextField name = createUserTextField("Name", "name", true);
            TextField surname = createUserTextField("Surname", "surname", true);
            TextField organization = createUserTextField("Organization", "organization", false);
            TextField position = createUserTextField("Position", "position", false);
            TextField email = createUserTextField("Email", "email", true);
//            TextField country = createUserTextField("Country", "country", true);
            TextField state = createUserTextField("State", "state", false);
            TextField city = createUserTextField("City", "city", true);
            TextField postcode = createUserTextField("Postcode", "postcode",true);
            ExpandingTextArea address = fieldGroup.buildAndBind("Address", "address", ExpandingTextArea.class);
            address.setRequiredError("Address should not be null");
            address.setRequired(true);
            address.addValidator(new StringLengthValidator("Filed length must be less than or equal to 1024", 0, 1024, true));
            address.setNullRepresentation("");
            TextField phoneNumber = createUserTextField("Phone Number", "phoneNumber", true);

            Button save = new Button("Save", event -> {
                try {
                    fieldGroup.getFields().forEach(Validatable::validate);
                    fieldGroup.commit();
                    service.save(fieldGroup.getItemDataSource().getBean());
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
            layout.addComponents(name, surname, organization, position, email, country, state, city, postcode, phoneNumber, address, save);
            layout.setComponentAlignment(save, Alignment.MIDDLE_CENTER);
            layout.setMargin(true);
            layout.setSpacing(true);

            setCaption("Add User");
            setContent(layout);
            center();
            setModal(true);
            setResizable(false);
        }

        private TextField createUserTextField(String caption, String propertyId, boolean required) {
            TextField result = fieldGroup.buildAndBind(caption, propertyId, TextField.class);
            result.setNullRepresentation("");
            if (required) {
                result.setRequiredError(caption + " should not be null");
                result.setRequired(true);
            }
            result.addValidator(new StringLengthValidator("Filed length must be less than or equal to 255", 0, 255, true));
            return result;
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
