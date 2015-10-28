package ru.oxygensoftware.backoffice.ui.view.invite;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;
import ru.oxygensoftware.backoffice.data.Invite;
import ru.oxygensoftware.backoffice.data.Product;
import ru.oxygensoftware.backoffice.service.InviteService;
import ru.oxygensoftware.backoffice.service.ProductService;

import javax.annotation.PostConstruct;
import java.util.Collection;

@SpringView(name = InviteTableView.NAME)
@SuppressWarnings("unchecked")
public class InviteTableView extends VerticalLayout implements View {
    public static final String NAME = "Table";
    @Autowired
    private InviteService service;
    @Autowired
    private ProductService productService;
    private BeanItemContainer<Invite> container = new BeanItemContainer<>(Invite.class);

    @PostConstruct
    public void init() {
        container.addAll(service.getAll());
        container.addNestedContainerProperty("product.name");
        FilterTable table = new FilterTable();
        table.setContainerDataSource(container);
        table.setVisibleColumns("id", "invite", "product.name", "dateCreated", "dateExpire", "dateActivated", "user",
                "comment");
        table.setColumnHeaders("ID", "Invite", "Product Name", "Creation Date", "Expiration Date", "Activation Date",
                "User", "Comment");
        table.setSizeFull();
        table.setWidth("100%");
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setFilterBarVisible(true);

        Button generate = new Button("Generate", event -> {
            UI.getCurrent().addWindow(new GenerateInvitesWindow());
        });

        Button delete = new Button("Delete", event -> {
            Object value = table.getValue();
            if (value != null) {
                ((Collection<Invite>) value).stream().forEach(item -> service.delete(item.getId()));
                refresh();
                table.setValue(null);
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(generate, delete);
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

    private class GenerateInvitesWindow extends Window {
        public GenerateInvitesWindow() {
            BeanFieldGroup<InviteDto> fieldGroup = new BeanFieldGroup<>(InviteDto.class);
            fieldGroup.setItemDataSource(new InviteDto());

            ComboBox product = new ComboBox("Product");
            product.setContainerDataSource(new BeanItemContainer<>(Product.class, productService.getAll()));
            product.setItemCaptionPropertyId("name");
            product.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            product.setNullSelectionAllowed(false);
            product.setRequired(true);
            product.setImmediate(true);
            product.setRequiredError("Select a product");
            fieldGroup.bind(product, "product");

            DateField dateExpire = fieldGroup.buildAndBind("Expiration date", "dateExpire", DateField.class);
            dateExpire.setRequired(true);
            dateExpire.setImmediate(true);
            dateExpire.setRequiredError("You should select expiration date");

            TextField amount = fieldGroup.buildAndBind("Amount", "amount", TextField.class);
            amount.setRequired(true);
            amount.setRequiredError("You should specify an amount of invites");
            amount.setNullRepresentation("");
            amount.setImmediate(true);

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
        }
    }
}
