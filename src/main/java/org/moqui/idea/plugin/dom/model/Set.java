package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Set extends DomElement {
    
    String TAG_NAME = "set";
    
    String ATTR_FIELD = "field";
    String ATTR_FROM = "from";
    @NotNull
    @Attribute(ATTR_FIELD)
    GenericAttributeValue<String> getField();

    @NotNull
    @Attribute(ATTR_FROM)
    GenericAttributeValue<String> getFrom();

    @NotNull GenericAttributeValue<String> getValue();
    @NotNull GenericAttributeValue<String> getDefaultValue();
    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getSetIfEmpty();
}
