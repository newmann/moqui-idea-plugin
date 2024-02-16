package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface ListOptions extends DomElement {
    public static final String TAG_NAME = "list-options";


    @NotNull GenericAttributeValue<String> getList();
    @NotNull GenericAttributeValue<String> getKey();
    @NotNull GenericAttributeValue<String> getText();
}
