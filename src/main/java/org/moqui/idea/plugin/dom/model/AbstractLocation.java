package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

public interface AbstractLocation extends DomElement {
    public static final String ATTR_LOCATION = "location";

    @NotNull
    @Attribute(ATTR_LOCATION)
    @Convert(LocationConverter.class)
    GenericAttributeValue<String> getLocation();
}
