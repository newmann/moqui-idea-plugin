package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameReferenceConverter;

public interface KeyMap extends DomElement {
    
    String TAG_NAME = "key-map";

    
    String ATTR_FIELD_NAME ="field-name";

    
    String ATTR_RELATED ="related";

//    String ATTR_JOIN_FROM_ALIAS ="join-from-alias";

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
