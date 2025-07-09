package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameReferenceConverter;

public interface KeyValue extends DomElement {

    String TAG_NAME = "key-value";


    String ATTR_RELATED = "related";
    String ATTR_VALUE = "value";

    @NotNull
    @Attribute(ATTR_RELATED)
    @Referencing(EntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getRelated();

    @NotNull
    @Attribute(ATTR_VALUE)
    GenericAttributeValue<String> getValue();

}
