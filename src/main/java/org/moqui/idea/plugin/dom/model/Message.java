package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Message extends DomElement {
    
    String TAG_NAME = "message";

    @NotNull
    GenericAttributeValue<String> getType();

    @NotNull
    GenericAttributeValue<Boolean> getPublic();
    @NotNull
    GenericAttributeValue<Boolean> getError();
}
