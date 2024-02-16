package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Return extends DomElement {
    public static final String TAG_NAME = "return";
    @NotNull
    GenericAttributeValue<String> getMessage();

    @NotNull
    GenericAttributeValue<String> getType();

    @NotNull
    GenericAttributeValue<Boolean> getPublic();
    @NotNull
    GenericAttributeValue<Boolean> getError();
}
