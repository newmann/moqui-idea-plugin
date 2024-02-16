package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ErrorScreen extends DomElement {
    public static final String TAG_NAME = "error-screen";

    @NotNull
    GenericAttributeValue<String> getError();
    @NotNull
    GenericAttributeValue<Boolean> getScreenPath();


}
