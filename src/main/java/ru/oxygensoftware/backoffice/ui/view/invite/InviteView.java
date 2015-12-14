package ru.oxygensoftware.backoffice.ui.view.invite;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.*;
import com.vaadin.data.validator.LongRangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterGenerator;
import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.datefilter.DateInterval;
import ru.oxygensoftware.backoffice.data.Invite;
import ru.oxygensoftware.backoffice.data.Product;
import ru.oxygensoftware.backoffice.service.InviteService;
import ru.oxygensoftware.backoffice.service.ProductService;
import ru.oxygensoftware.backoffice.util.ConfirmDialog;
import ru.oxygensoftware.backoffice.util.Constant;
import ru.oxygensoftware.backoffice.util.MyFieldGroupFieldFactory;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        container.addNestedContainerProperty("product.productCode");
        container.addNestedContainerProperty("user.email");
        FilterTable table = new FilterTable();
        table.setContainerDataSource(container);
        table.setVisibleColumns("id", "invite", "product.name", "dateCreated", "dateExpire", "dateActivated", "user.email",
                "comment");
        table.setColumnHeaders("ID", "Invite", "Product Name", "Creation Date", "Expiration Date", "Activation Date",
                "User Email", "Comment");
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
        table.setConverter("dateExpire", new Converter<String, Date>() {
            private final String pattern = "MMM dd, yyyy";

            @Override
            public Date convertToModel(String value, Class<? extends Date> targetType, Locale locale) throws ConversionException {
                if (locale == null) {
                    locale = Locale.getDefault();
                }
                SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
                try {
                    return formatter.parse(value);
                } catch (ParseException e) {
                    return null;
                }
            }

            @Override
            public String convertToPresentation(Date value, Class<? extends String> targetType, Locale locale) throws ConversionException {
                if (locale == null) {
                    locale = Locale.getDefault();
                }
                SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
                return formatter.format(value);
            }

            @Override
            public Class<Date> getModelType() {
                return Date.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
        table.setFilterGenerator(new FilterGenerator() {
            @Override
            public Container.Filter generateFilter(Object propertyId, Object value) {
                if (value instanceof DateInterval) {
                    DateInterval interval = (DateInterval) value;
                    Comparable<?> actualFrom = interval.getFrom(), actualTo = interval
                            .getTo();
                    actualFrom = actualFrom == null ? null : new Timestamp(interval
                            .getFrom().getTime());
                    actualTo = actualTo == null ? null : new Timestamp(interval
                            .getTo().getTime());
                    if (actualFrom != null && actualTo != null) {
                        return new Between(propertyId, actualFrom, actualTo);
                    } else if (actualFrom != null) {
                        return new And(new Compare.GreaterOrEqual(propertyId, actualFrom), new Not(new IsNull(propertyId)));
                    } else {
                        return new And(new Compare.LessOrEqual(propertyId, actualTo), new Not(new IsNull(propertyId)));
                    }
                }

                return null;
            }

            @Override
            public Container.Filter generateFilter(Object o, Field<?> field) {
                return null;
            }

            @Override
            public AbstractField<?> getCustomFilterComponent(Object o) {
                return null;
            }

            @Override
            public void filterRemoved(Object o) {

            }

            @Override
            public void filterAdded(Object o, Class<? extends Container.Filter> aClass, Object o1) {

            }

            @Override
            public Container.Filter filterGeneratorFailed(Exception e, Object o, Object o1) {
                return null;
            }
        });

        Button generate = new Button("Generate", event -> {
            openWindow(new GenerateInvitesWindow());
        });

        Button delete = new Button("Delete", event -> {
            Object value = table.getValue();
            if (value != null) {
                Collection<Invite> col = (Collection<Invite>) value;
                String entr = col.size() == 1 ? " entry?" : " entries?";
                ConfirmDialog dialog = new ConfirmDialog("Do you really want to delete " + col.size() + entr, confirmEvent -> {
                    col.stream().forEach(item -> service.delete(item.getId()));
                    refresh();
                    table.setValue(null);
                });
                getUI().addWindow(dialog);
            }
        });

        Button edit = new Button("Edit", event -> {
            if (table.getValue() != null) {
                Set<Invite> invites = (Set<Invite>) table.getValue();
                if (invites.size() == 1) {
                    List<Invite> list = new ArrayList<>(invites);
                    openWindow(new EditInviteWindow(list.get(0)));
                } else if (invites.size() > 1) {
                    openWindow(new EditInviteWindow(new MultipleInviteDto(invites)));
                }
            }
        });

        Button export = new Button("Export to CSV");
        FileDownloader fd = new FileDownloader(new StreamResource(() -> {
            try {
                final String DELIM = ";";
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                final PrintWriter writer = new PrintWriter(bos);
                table.getItemIds().forEach(id -> {
                    Item item = table.getItem(id);
                    StringBuilder sb = new StringBuilder();
                    sb.append("\"").append(item.getItemProperty("id").getValue()).append("\"").append(DELIM);
                    sb.append("\"").append(item.getItemProperty("invite").getValue()).append("\"").append(DELIM);
                    sb.append("\"").append(item.getItemProperty("dateCreated").getValue()).append("\"").append(DELIM);
                    sb.append("\"").append(item.getItemProperty("dateExpire").getValue()).append("\"").append(DELIM);
                    sb.append("\"").append(item.getItemProperty("dateActivated").getValue()).append("\"").append(DELIM);
                    sb.append("\"").append(item.getItemProperty("user.email").getValue()).append("\"").append(DELIM);
                    sb.append("\"").append(item.getItemProperty("product.productCode").getValue()).append("\"").append(DELIM);
                    sb.append("\"").append(item.getItemProperty("comment").getValue()).append("\"");
                    Pattern pattern = Pattern.compile("\"null\"");
                    Matcher matcher = pattern.matcher(sb);
                    writer.println(matcher.replaceAll("\"\""));
                });
                writer.flush();
                return new ByteArrayInputStream(bos.toByteArray());
            } catch (Exception e) {
                Notification.show("Fail!");
            }
            return null;
        }, "export.csv"));
        fd.extend(export);

        HorizontalLayout buttonLayout = new HorizontalLayout(generate, edit, delete, export);
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
            fieldGroup.setFieldFactory(new MyFieldGroupFieldFactory());

            ComboBox product = createProduct();
            fieldGroup.bind(product, "product");
            product.setWidth(Constant.STANDARD_FIELD_WIDTH);

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
        public EditInviteWindow(MultipleInviteDto source) {
            BeanFieldGroup<MultipleInviteDto> fieldGroup = new BeanFieldGroup<>(MultipleInviteDto.class);
            fieldGroup.setItemDataSource(source);

            ComboBox product = createProduct();
            fieldGroup.bind(product, "product");
            product.setWidth(Constant.STANDARD_FIELD_WIDTH);
            product.setNullSelectionAllowed(true);
            product.setRequired(false);
            product.setRequiredError(null);

            DateField dateExpire = fieldGroup.buildAndBind("Expiration Date", "dateExpire", DateField.class);
            dateExpire.setRequiredError("You should select expiration date");
            dateExpire.addValidator(value -> {
                if (value != null) {
                    Date date = (Date) value;
                    if (date.before(new Date())) {
                        throw new Validator.InvalidValueException("Expiration date should be after current date");
                    }
                }
            });

            TextField comment = fieldGroup.buildAndBind("Comment", "comment", TextField.class);
            comment.setNullRepresentation("");

            Button save = new Button("Save", event -> {
                try {
                    fieldGroup.commit();
                    MultipleInviteDto dto = fieldGroup.getItemDataSource().getBean();
                    dto.getInvites().forEach(invite -> {
                        if (dto.getComment() != null) {
                            invite.setComment(dto.getComment());
                        }
                        if (dto.getDateExpire() != null) {
                            invite.setDateExpire(dto.getDateExpire());
                        }
                        if (dto.getProduct() != null) {
                            invite.setProduct(dto.getProduct());
                        }
                        service.save(invite);
                    });
                    refresh();
                    this.close();
                } catch (FieldGroup.CommitException e) {
                    Notification.show(e.getMessage());
                }
            });

            FormLayout layout = new FormLayout(product, dateExpire, comment, save);
            layout.setSizeUndefined();
            layout.setMargin(true);
            layout.setSpacing(true);

            setCaption("Edit Invites");
            setContent(layout);
            center();
            setModal(true);
            setResizable(false);
        }

        public EditInviteWindow(Invite source) {
            BeanFieldGroup<Invite> fieldGroup = new BeanFieldGroup<>(Invite.class);
            fieldGroup.setItemDataSource(source);
            fieldGroup.setFieldFactory(new MyFieldGroupFieldFactory());

            TextField invite = fieldGroup.buildAndBind("Invite", "invite", TextField.class);
            invite.setEnabled(false);

            ComboBox product = createProduct();
            product.setWidth(Constant.STANDARD_FIELD_WIDTH);
            fieldGroup.bind(product, "product");

            DateField dateCreated = fieldGroup.buildAndBind("Creation Date", "dateCreated", DateField.class);
            dateCreated.setEnabled(false);

            DateField dateExpire = fieldGroup.buildAndBind("Expiration date", "dateExpire", DateField.class);
            dateExpire.setRequired(true);
            dateExpire.setImmediate(true);
            dateExpire.setResolution(Resolution.DAY);
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
            user.setWidth(Constant.STANDARD_FIELD_WIDTH);

            TextArea comment = fieldGroup.buildAndBind("Comment", "comment", TextArea.class);
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
