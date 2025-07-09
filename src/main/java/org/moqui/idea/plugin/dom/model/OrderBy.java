package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.MultiEntityFieldNameReferenceConverter;

public interface OrderBy extends DomElement {

    String TAG_NAME = "order-by";

    String ATTR_FIELD_NAME = "field-name";

    @NotNull
    @Attribute(ATTR_FIELD_NAME)
    @Referencing(MultiEntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getFieldName();


}
