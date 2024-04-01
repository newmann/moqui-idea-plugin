package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameConverter;

public interface KeyValue extends DomElement {
    public static final String TAG_NAME = "key-value";

    public static final String ATTR_RELATED = "related";
    public static final String ATTR_VALUE = "value";

    @NotNull
    @Attribute(ATTR_RELATED)
    @Convert(EntityFieldNameConverter.class)
    GenericAttributeValue<String> getRelated();

    @NotNull
    @Attribute(ATTR_VALUE)
    GenericAttributeValue<String> getValue();

}
