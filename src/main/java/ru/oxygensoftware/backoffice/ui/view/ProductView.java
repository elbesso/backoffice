package ru.oxygensoftware.backoffice.ui.view;

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
            setCaption("Add Product");
            setWidth("300px");
            setHeight("150px");
            FormLayout layout = new FormLayout();
            layout.setSizeFull();
            TextField textField = new TextField();
            Button save = new Button("Save", event -> {
                Product newProduct = service.create(textField.getValue());
                service.save(newProduct);
                this.close();
                refresh();
            });
            save.setWidthUndefined();
            layout.addComponents(textField, save);
            layout.setComponentAlignment(save, Alignment.MIDDLE_CENTER);
            setContent(layout);
            center();
            setModal(true);
        }
    }
}
