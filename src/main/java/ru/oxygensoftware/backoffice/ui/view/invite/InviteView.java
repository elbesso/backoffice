package ru.oxygensoftware.backoffice.ui.view.invite;

import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.LongRangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;
import org.vaadin.hene.expandingtextarea.ExpandingTextArea;
import ru.oxygensoftware.backoffice.data.Invite;
import ru.oxygensoftware.backoffice.data.Product;
import ru.oxygensoftware.backoffice.service.InviteService;
import ru.oxygensoftware.backoffice.service.ProductService;

import javax.annotation.PostConstruct;
import java.util.*;

@SpringView(name = InviteView.NAME)
@SuppressWarnings("unchecked")
public class InviteView extends VerticalLayout implements View {
    public static final String NAME = "Invite";
    @Autowired
    private InviteService service;
    @Autowired
    private ProductService productService;
    private BeanItemContainer<Invite> container = new BeanItemContainer<>(Invite.class);

    @PostConstruct
    public void init() {
        container.addAll(service.getAll());
        container.addNestedContainerProperty("product.name");
        container.addNestedContainerProperty("user.name");
        FilterTable table = new FilterTable();
        table.setContainerDataSource(container);
        table.setVisibleColumns("id", "invite", "product.name", "dateCreated", "dateExpire", "dateActivated", "user.name",
                "comment");
        table.setColumnHeaders("ID", "Invite", "Product Name", "Creation Date", "Expiration Date", "Activation Date",
                "User", "Comment");
        table.setSizeFull();
        table.setWidth("100%");
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setFilterBarVisible(true);
        table.addItemClickListener(event -> {
            if (event.isDoubleClick()) {
                if (event.getItem() != null) {
                    BeanItem<Invite> item = (BeanItem<Invite>) event.getItem();
                    openWindow(new EditInviteWindow(item.getBean()));
                }
            }
        });

        Button generate = new Button("Generate", event -> {
            openWindow(new GenerateInvitesWindow());
        });

        Button delete = new Button("Delete", event -> {
            Object value = table.getValue();
            if (value != null) {
                ((Collection<Invite>) value).stream().forEach(item -> service.delete(item.getId()));
                refresh();
                table.setValue(null);
            }
        });

        Button edit = new Button("Edit", event -> {
            if (table.getValue() != null) {
                Set<Invite> invites = (Set<Invite>) table.getValue();
                if (invites.size() == 1) {
                    List<Invite> list = new ArrayList<>(invites);
                    openWindow(new EditInviteWindow(list.get(0)));
                }
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(generate, edit, delete);
        buttonLayout.setSizeUndefined();
        buttonLayout.setSpacing(true);

        setSizeFull();
        setMargin(true);
        setSpacing(true);
        addComponents(buttonLayout, table);
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

    private ComboBox createProduct() {
        ComboBox result = new ComboBox("Product");
        result.setContainerDataSource(new BeanItemContainer<>(Product.class, productService.getAll()));
        result.setItemCaptionPropertyId("name");
        result.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        result.setNullSelectionAllowed(false);
        result.setRequired(true);
        result.setImmediate(true);
        result.setRequiredError("Select a product");
        return result;
    }

    private class GenerateInvitesWindow extends Window {
        public GenerateInvitesWindow() {
            BeanFieldGroup<InviteDto> fieldGroup = new BeanFieldGroup<>(InviteDto.class);
            fieldGroup.setItemDataSource(new InviteDto());

            ComboBox product = createProduct();
            fieldGroup.bind(product, "product");

            DateField dateExpire = fieldGroup.buildAndBind("Expiration Date", "dateExpire", DateField.class);
            dateExpire.setRequired(true);
            dateExpire.setImmediate(true);
            dateExpire.setRequiredError("You should select expiration date");
            dateExpire.addValidator(value -> {
                if (value != null) {
                    Date date = (Date) value;
                    if (date.before(fieldGroup.getItemDataSource().getBean().getDateCreated())) {
                        throw new Validator.InvalidValueException("Expiration date should be after creation date");
                    }
                }
            });

            TextField amount = fieldGroup.buildAndBind("Amount", "amount", TextField.class);
            amount.setRequired(true);
            amount.setRequiredError("You should specify an amount of invites");
            amount.setNullRepresentation("");
            amount.setImmediate(true);
            amount.addValidator(new LongRangeValidator("Value must be long in a range from 1 to 100000", 1L, 100000L));

            TextField comment = fieldGroup.buildAndBind("Comment", "comment", TextField.class);
            comment.setNullRepresentation("");

            Button generate = new Button("Generate", event -> {
                try {
                    fieldGroup.commit();
                    InviteDto dto = fieldGroup.getItemDataSource().getBean();
                    service.generateInvites(dto.getAmount(), dto.getProduct(), dto.getDateExpire(), dto.getComment());
                    refresh();
                    this.close();
                } catch (FieldGroup.CommitException e) {
                    Notification.show(e.getMessage());
                }
            });

            FormLayout layout = new FormLayout(product, dateExpire, amount, comment, generate);
            layout.setSizeUndefined();
            layout.setComponentAlignment(generate, Alignment.MIDDLE_CENTER);
            layout.setMargin(true);
            layout.setSpacing(true);

            setCaption("Generate Invites");
            setContent(layout);
            center();
            setModal(true);
            setResizable(false);
        }
    }

    private class EditInviteWindow extends Window {
        public EditInviteWindow(Invite source) {
            BeanFieldGroup<Invite> fieldGroup = new BeanFieldGroup<>(Invite.class);
            fieldGroup.setItemDataSource(source);

            TextField invite = fieldGroup.buildAndBind("Invite", "invite", TextField.class);
            invite.setWidth("200px");
            invite.setEnabled(false);

            TextField product = fieldGroup.buildAndBind("Product", "product.name", TextField.class);
            product.setSizeFull();
            product.setEnabled(false);

            DateField dateCreated = fieldGroup.buildAndBind("Creation Date", "dateCreated", DateField.class);
            dateCreated.setEnabled(false);

            DateField dateExpire = fieldGroup.buildAndBind("Expiration date", "dateExpire", DateField.class);
            dateExpire.setRequired(true);
            dateExpire.setImmediate(true);
            dateExpire.setRequiredError("You should select expiration date");
            dateExpire.addValidator(value -> {
                if (value != null) {
                    Date date = (Date) value;
                    if (date.before(fieldGroup.getItemDataSource().getBean().getDateCreated())) {
                        throw new Validator.InvalidValueException("Expiration date should be after creation date");
                    }
                }
            });

            DateField dateActivated = fieldGroup.buildAndBind("Activation date", "dateActivated", DateField.class);
            dateActivated.setEnabled(false);

            TextField user = new TextField("User");
            if (source.getUser() != null) {
                fieldGroup.bind(user, "user.name");
            }
            user.setEnabled(false);
            user.setNullRepresentation("");

            ExpandingTextArea comment = fieldGroup.buildAndBind("Comment", "comment", ExpandingTextArea.class);
            comment.setNullRepresentation("");
            comment.addValidator(new StringLengthValidator("Filed length must be less than or equal to 1024", 0, 1024, true));

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
                    e.printStackTrace();
                    Notification.show(e.getMessage());
                }
            });
            save.setWidthUndefined();

            FormLayout layout = new FormLayout(invite, product, dateCreated, dateExpire, dateActivated, user, comment, save);
            layout.setSizeUndefined();
            layout.setComponentAlignment(save, Alignment.MIDDLE_CENTER);
            layout.setMargin(true);
            layout.setSpacing(true);

            setCaption("Edit Invite");
            setContent(layout);
            center();
            setModal(true);
            setResizable(false);
        }
    }
}
