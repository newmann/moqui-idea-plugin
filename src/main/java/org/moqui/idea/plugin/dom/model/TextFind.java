package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface TextFind extends DomElement {
    public static final String TAG_NAME = "text-find";
    @NotNull GenericAttributeValue<String> getSize();
    @NotNull GenericAttributeValue<String> getMaxLength();
    @NotNull GenericAttributeValue<String> getDefaultValue();
    @NotNull GenericAttributeValue<String> getIgnoreCase();
    @NotNull GenericAttributeValue<String> getDefaultOperator();
    @NotNull GenericAttributeValue<String> getHideOptions();


}
