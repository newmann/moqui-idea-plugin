package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface VisibleWhen extends DomElement {

    public static final String TAG_NAME = "visible-when";

    @NotNull GenericAttributeValue<String> getField();
    @NotNull GenericAttributeValue<String> getValue();
    @NotNull GenericAttributeValue<String> getFrom();

}
