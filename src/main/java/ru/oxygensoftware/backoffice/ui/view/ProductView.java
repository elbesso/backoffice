package ru.oxygensoftware.backoffice.ui.view;

import com.vaadin.data.Validatable;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.oxygensoftware.backoffice.data.Product;
import ru.oxygensoftware.backoffice.service.ProductService;

import javax.annotation.PostConstruct;
import java.util.Collection;

@SpringView(name = ProductView.NAME)
public class ProductView extends VerticalLayout implements View {
    public final static String NAME = "Product";
    BeanItemContainer<Product> container = new BeanItemContainer<>(Product.class);
    @Autowired
    private ProductService service;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        container.addAll(service.getAll());
        Table table = new Table();
        table.setContainerDataSource(container);
        table.setVisibleColumns("id", "name");
        table.setColumnHeaders("ID", "Product Name");
        table.setSizeFull();
        table.setSelectable(true);
        table.setMultiSelect(true);

        Button add = new Button();
        add.addClickListener(event -> UI.getCurrent().addWindow(new AddProductWindow()));
        add.setCaption("Add");

        Button delete = new Button("Delete", event -> {
            Object value = table.getValue();
            if (value != null) {
                ((Collection<Product>) value).stream().forEach(item -> service.delete(item.getId()));
                refresh();
                table.setValue(null);
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(add, delete);
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

    private class AddProductWindow extends Window {
        public AddProductWindow() {
            BeanFieldGroup<Product> fieldGroup = new BeanFieldGroup<>(Product.class);
            fieldGroup.setItemDataSource(service.create());

            TextField name = fieldGroup.buildAndBind("Product Name", "name", TextField.class);
            name.setNullRepresentation("");
            name.setRequiredError("Product name should not be null");
            name.setRequired(true);

            Button save = new Button("Save", event -> {
                try {
                    fieldGroup.getFields().forEach(Validatable::validate);
                    fieldGroup.commit();
                    service.save(fieldGroup.getItemDataSource().getBean());
                    this.close();
                    refresh();
                } catch (FieldGroup.CommitException e) {
                    Notification.show(e.getMessage());
                }
            });
            save.setWidthUndefined();

            FormLayout layout = new FormLayout();
            layout.setSizeUndefined();
            layout.addComponents(name, save);
            layout.setComponentAlignment(save, Alignment.MIDDLE_CENTER);
            layout.setMargin(true);
            layout.setSpacing(true);

            setCaption("Add Product");
            setContent(layout);
            center();
            setModal(true);
        }
    }
}
