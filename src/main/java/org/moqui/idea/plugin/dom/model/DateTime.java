package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface DateTime extends DomElement {
    
    public static final String TAG_NAME = "date-time";


    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getFormat();
    @NotNull GenericAttributeValue<String> getDefaultValue();
    @NotNull GenericAttributeValue<String> getSize();
    @NotNull GenericAttributeValue<String> getMaxLength();
    @NotNull GenericAttributeValue<String> getAutoYear();
    @NotNull GenericAttributeValue<String> getMinuteStepping();
}
