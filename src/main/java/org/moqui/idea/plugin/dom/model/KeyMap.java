package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameReferenceConverter;

public interface KeyMap extends DomElement {
    
    public static final String TAG_NAME = "key-map";

    
    public static final String ATTR_FIELD_NAME ="field-name";

    
    public static final String ATTR_RELATED ="related";

//    public static final String ATTR_JOIN_FROM_ALIAS ="join-from-alias";

    @NotNull
    @Attribute(ATTR_FIELD_NAME)
    @Referencing(EntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getFieldName();

    @NotNull
    @Attribute(ATTR_RELATED)
    @Referencing(EntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getRelated();

    @NotNull
    GenericAttributeValue<String> getRelatedFieldName();

}
