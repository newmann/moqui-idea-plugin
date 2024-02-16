package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DateFind extends DomElement {
    public static final String TAG_NAME = "date-find";


    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getFormat();
    @NotNull GenericAttributeValue<String> getDefaultValueFrom();
    @NotNull GenericAttributeValue<String> getDefaultValueThru();
}
