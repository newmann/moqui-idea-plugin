package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface IncludeScreen extends DomElement {
    public static final String TAG_NAME = "include-screen";

    @NotNull GenericAttributeValue<String> getLocation();
    @NotNull GenericAttributeValue<String> getShareScope();

}
