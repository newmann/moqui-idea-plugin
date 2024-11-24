package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface EntityData extends DomElement {
    public static final String TAG_NAME = "entity-data";

    @NotNull
//    @Convert(LocationConverter.class)
    GenericAttributeValue<String> getLocation();

    @NotNull
    GenericAttributeValue<String> getTimeout();
    @NotNull
    GenericAttributeValue<String> getMode();

}
