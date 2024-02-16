package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Text extends DomElement {
    public static final String TAG_NAME = "text";

    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getLocation();
    @NotNull GenericAttributeValue<String> getTemplate();
    @NotNull GenericAttributeValue<String> getEncode();
    @NotNull GenericAttributeValue<String> getNoBoundaryComment();
}
