package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Log extends DomElement {
    String TAG_NAME = "log";

    @NotNull GenericAttributeValue<String> getLevel();
    @NotNull GenericAttributeValue<String> getMessage();
}
