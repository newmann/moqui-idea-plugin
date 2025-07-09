package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface RangeFind extends DomElement {

    String TAG_NAME = "range-find";

    @NotNull GenericAttributeValue<String> getSize();
    @NotNull GenericAttributeValue<String> getMaxlength();
    @NotNull GenericAttributeValue<String> getDefaultValueFrom();
    @NotNull GenericAttributeValue<String> getDefaultValueThru();
}
