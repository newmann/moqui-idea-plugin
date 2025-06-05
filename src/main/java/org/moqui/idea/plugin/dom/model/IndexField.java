package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameReferenceConverter;

public interface IndexField extends DomElement {
    
    public static final String TAG_NAME = "index-field";

    public static final String ATTR_NAME ="name";

    @NotNull
    @Attribute(ATTR_NAME)
    @Referencing(EntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getName();

}
