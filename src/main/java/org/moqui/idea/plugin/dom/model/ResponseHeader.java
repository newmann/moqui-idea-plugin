package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ResponseHeader extends DomElement {

    String TAG_NAME = "response-header";

    @NotNull GenericAttributeValue<String> getType();

    @NotNull
    GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getValue();
    @NotNull GenericAttributeValue<Boolean> getAdd();



}
