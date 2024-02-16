package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface EntitySet extends DomElement {
    public static final String TAG_NAME = "entity-set";

    @NotNull GenericAttributeValue<String> getValueField();
    @NotNull GenericAttributeValue<String> getMap();
    @NotNull GenericAttributeValue<String> getPrefix();

    @NotNull GenericAttributeValue<String> getInclude();
    @NotNull GenericAttributeValue<Boolean> getSetIfEmpty();


}
