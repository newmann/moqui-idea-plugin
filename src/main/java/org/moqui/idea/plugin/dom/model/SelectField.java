package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.MultiEntityFieldNameConverter;

public interface SelectField extends DomElement {
    public static final String TAG_NAME = "select-field";


    @NotNull
    @Convert(MultiEntityFieldNameConverter.class)
    GenericAttributeValue<String> getFieldName();

}
