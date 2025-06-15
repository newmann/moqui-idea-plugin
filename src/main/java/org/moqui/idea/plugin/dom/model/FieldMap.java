package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameReferenceConverter;

public interface FieldMap extends DomElement {

    public static final String TAG_NAME = "field-map";

    public static final String ATTR_FIELD_NAME = "field-name";

    @NotNull
    @Required
    @Attribute(ATTR_FIELD_NAME)
    @Referencing(EntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getFieldName();

    @NotNull
    GenericAttributeValue<String> getFrom();

    @NotNull
    GenericAttributeValue<String> getValue();

}
