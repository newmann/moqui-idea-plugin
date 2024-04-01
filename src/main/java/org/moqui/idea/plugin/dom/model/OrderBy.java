package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.MultiEntityFieldNameConverter;

public interface OrderBy extends DomElement {
    public static final String TAG_NAME = "order-by";
    public static final String ATTR_FIELD_NAME = "field-name";

    @NotNull
    @Attribute(ATTR_FIELD_NAME)
    @Convert(MultiEntityFieldNameConverter.class)
    GenericAttributeValue<String> getFieldName();


}
