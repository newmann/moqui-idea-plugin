package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.MultiEntityFieldNameReferenceConverter;

public interface SelectField extends DomElement {
    
    String TAG_NAME = "select-field";
    
    String ATTR_FIELD_NAME = "field-name";


    @NotNull
    @Referencing(MultiEntityFieldNameReferenceConverter.class)
    @Attribute(ATTR_FIELD_NAME)
    GenericAttributeValue<String> getFieldName();

}
