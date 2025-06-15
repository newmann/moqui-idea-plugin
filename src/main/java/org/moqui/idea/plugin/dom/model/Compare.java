package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface Compare extends AllOperationsList {
    
    public static final String TAG_NAME = "compare";

    @NotNull GenericAttributeValue<String> getField();
    @NotNull GenericAttributeValue<String> getOperator();
    @NotNull GenericAttributeValue<String> getValue();
    @NotNull GenericAttributeValue<String> getToField();
    @NotNull GenericAttributeValue<String> getFormat();
    @NotNull GenericAttributeValue<String> getType();

    @NotNull
    @SubTag(Else.TAG_NAME)
    Else getElse();

}
