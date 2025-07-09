package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface DateFind extends DomElement {

    String TAG_NAME = "date-find";


    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getFormat();
    @NotNull GenericAttributeValue<String> getDefaultValueFrom();
    @NotNull GenericAttributeValue<String> getDefaultValueThru();
}
