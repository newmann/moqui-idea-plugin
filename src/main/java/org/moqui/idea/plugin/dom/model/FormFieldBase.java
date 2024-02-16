package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface FormFieldBase extends DomElement {
    @NotNull GenericAttributeValue<String> getName();

    @NotNull GenericAttributeValue<String> getFrom();

    @NotNull
    GenericAttributeValue<String> getHide();

}
