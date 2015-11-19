package ru.oxygensoftware.backoffice.util;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.ui.Field;

/**
 * Created by Dmitry Raguzin
 * Date: 19.11.15
 */
public class MyFieldGroupFieldFactory extends DefaultFieldGroupFieldFactory {

    @Override
    public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
        T field = super.createField(type, fieldType);
        field.setWidth(Constant.STANDARD_FIELD_WIDTH);
        return field;
    }
}
