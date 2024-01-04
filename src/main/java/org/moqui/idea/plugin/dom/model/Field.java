package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Field extends DomElement {
    public static final String TAG_NAME = "field";
    @NotNull
    @Attribute("name")
    GenericAttributeValue<String> getName();

    @NotNull
    @Attribute("type")
    GenericAttributeValue<String> getType();

    @NotNull
    @Attribute("default")
    GenericAttributeValue<String> getDefault();

    @NotNull
    @Attribute("is-pk")
    GenericAttributeValue<Boolean> getIsPk();
}
