package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Option extends DomElement {

    String TAG_NAME = "option";
    @NotNull GenericAttributeValue<String> getKey();
    @NotNull GenericAttributeValue<String> getText();
}
