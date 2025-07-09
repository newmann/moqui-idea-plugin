package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameReferenceConverter;

public interface IndexField extends DomElement {
    
    String TAG_NAME = "index-field";

    String ATTR_NAME ="name";

    @NotNull
    @Attribute(ATTR_NAME)
    @Referencing(EntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getName();

}
