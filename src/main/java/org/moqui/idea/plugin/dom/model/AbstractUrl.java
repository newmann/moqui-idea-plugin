package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;
import org.moqui.idea.plugin.dom.converter.UrlConverter;

public interface AbstractUrl extends DomElement {
    public static final String ATTR_URL = "url";

    @NotNull
    @Attribute(ATTR_URL)
    @Convert(LocationConverter.class)
    GenericAttributeValue<String> getUrl();



}
