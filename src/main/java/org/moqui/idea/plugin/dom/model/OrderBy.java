package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.MultiEntityFieldNameReferenceConverter;

public interface OrderBy extends DomElement {
    public static final String TAG_NAME = "order-by";
    public static final String ATTR_FIELD_NAME = "field-name";

    @NotNull
    @Attribute(ATTR_FIELD_NAME)
    @Referencing(MultiEntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getFieldName();


}
