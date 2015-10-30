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
import ru.oxygensoftware.backoffice.data.Product;
import ru.oxygensoftware.backoffice.service.ProductService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SpringView(name = ProductView.NAME)
public class ProductView extends VerticalLayout implements View {
    public final static String NAME = "Product";
    private BeanItemContainer<Product> container = new BeanItemContainer<>(Product.class);
    @Autowired
    private ProductService service;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        container.addAll(service.getAll());
        Table table = new Table();
        table.setContainerDataSource(container);
        table.setVisibleColumns("id", "productCode", "name");
        table.setColumnHeaders("ID", "Product Code", "Product Name");
        table.setSizeFull();
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.addItemClickListener(event -> {
            if (event.isDoubleClick()) {
                if (event.getItem() != null) {
                    BeanItem<Product> item = (BeanItem<Product>) event.getItem();
                    openWindow(new EditProductWindow(item.getBean()));
                }
            }
        });

        Button add = new Button();
        add.addClickListener(event -> openWindow(new EditProductWindow()));
        add.setCaption("Add");

        Button delete = new Button("Delete", event -> {
            Object value = table.getValue();
            if (value != null) {
                ((Collection<Product>) value).stream().forEach(item -> service.delete(item.getId()));
                refresh();
                table.setValue(null);
            }
        });

        Button edit = new Button("Edit", event -> {
            if (table.getValue() != null) {
                Set<Product> products = (Set<Product>) table.getValue();
                if (products.size() == 1) {
                    List<Product> list = new ArrayList<>(products);
                    openWindow(new EditProductWindow(list.get(0)));
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

    private class EditProductWindow extends Window {
        public EditProductWindow() {
            build(null);
            setCaption("Add Product");
        }

        public EditProductWindow(Product product) {
            build(product);
            setCaption("Edit Product");
        }

        private void build(Product product) {
            BeanFieldGroup<Product> fieldGroup = new BeanFieldGroup<>(Product.class);
            if (product == null) {
                fieldGroup.setItemDataSource(service.create());
            } else {
                fieldGroup.setItemDataSource(product);
            }

            TextField name = fieldGroup.buildAndBind("Product Name", "name", TextField.class);
            name.setNullRepresentation("");
            name.setRequiredError("Product name should not be null");
            name.setRequired(true);
            name.addValidator(new StringLengthValidator("Filed length must be less than or equal to 255", 0, 255, false));

            TextField productCode = fieldGroup.buildAndBind("Product Code", "productCode", TextField.class);
            productCode.setNullRepresentation("");
            productCode.addValidator(new StringLengthValidator("Field length must be less than or equal to 36", 0, 36,
                    true));
            productCode.setImmediate(true);

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
            layout.addComponents(name, productCode, save);
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
