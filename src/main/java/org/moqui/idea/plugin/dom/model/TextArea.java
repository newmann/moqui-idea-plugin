package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface TextArea extends DomElement {
    
    public static final String TAG_NAME = "text-area";

    @NotNull GenericAttributeValue<String> getCols();
    @NotNull GenericAttributeValue<String> getRows();
    @NotNull GenericAttributeValue<String> getMaxlength();
    @NotNull GenericAttributeValue<String> getDefaultValue();
    @NotNull GenericAttributeValue<String> getReadOnly();
    @NotNull GenericAttributeValue<String> getEditorType();
    @NotNull GenericAttributeValue<String> getEditorTheme();


}
