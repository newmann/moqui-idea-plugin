package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ServiceLocation extends DomElement {
    public static final String TAG_NAME = "service-location";

    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getLocation();



}
