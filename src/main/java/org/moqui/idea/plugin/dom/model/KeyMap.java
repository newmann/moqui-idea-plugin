package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameConverter;

public interface KeyMap extends DomElement {
    public static final String TAG_NAME = "key-map";

    public static final String ATTR_FIELD_NAME ="field-name";

    public static final String ATTR_RELATED ="related";

//    public static final String ATTR_JOIN_FROM_ALIAS ="join-from-alias";

    @NotNull
    @Attribute(ATTR_FIELD_NAME)
    @Convert(EntityFieldNameConverter.class)
    GenericAttributeValue<String> getFieldName();

    @NotNull
    @Attribute(ATTR_RELATED)
    @Convert(EntityFieldNameConverter.class)
    GenericAttributeValue<String> getRelated();

    @NotNull
    GenericAttributeValue<String> getRelatedFieldName();

}
