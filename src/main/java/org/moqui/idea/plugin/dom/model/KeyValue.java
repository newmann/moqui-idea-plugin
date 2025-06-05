package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameReferenceConverter;

public interface KeyValue extends DomElement {

    public static final String TAG_NAME = "key-value";


    public static final String ATTR_RELATED = "related";
    public static final String ATTR_VALUE = "value";

    @NotNull
    @Attribute(ATTR_RELATED)
    @Referencing(EntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getRelated();

    @NotNull
    @Attribute(ATTR_VALUE)
    GenericAttributeValue<String> getValue();

}
