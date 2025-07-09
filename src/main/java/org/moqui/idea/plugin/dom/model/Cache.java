package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Cache extends DomElement {
    String TAG_NAME = "cache";

    @NotNull
    GenericAttributeValue<String> getName();
    @NotNull
    GenericAttributeValue<String> getKeyType();
    @NotNull
    GenericAttributeValue<String> getValueType();
    @NotNull
    GenericAttributeValue<String> getExpireTimeIdle();
    @NotNull
    GenericAttributeValue<String> getExpireTimeLive();
    @NotNull
    GenericAttributeValue<String> getMaxElements();
    @NotNull
    GenericAttributeValue<String> getEvictionStrategy();
    @NotNull
    GenericAttributeValue<String> getType();

}
