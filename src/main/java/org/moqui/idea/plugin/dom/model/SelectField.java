package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.MultiEntityFieldNameReferenceConverter;

public interface SelectField extends DomElement {
    
    public static final String TAG_NAME = "select-field";
    
    public static final String ATTR_FIELD_NAME = "field-name";


    @NotNull
    @Referencing(MultiEntityFieldNameReferenceConverter.class)
    @Attribute(ATTR_FIELD_NAME)
    GenericAttributeValue<String> getFieldName();

}
