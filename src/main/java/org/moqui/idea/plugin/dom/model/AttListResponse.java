package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;
import org.moqui.idea.plugin.dom.converter.UrlConverter;

public interface AttListResponse extends AbstractUrl {

//    public static final String ATTR_URL = "url";

    @NotNull
    GenericAttributeValue<String> getType();

    @NotNull
    GenericAttributeValue<String> getUrlType();
    @NotNull
    GenericAttributeValue<String> getParameterMap();
    @NotNull
    GenericAttributeValue<Boolean> getSaveCurrentScreen();
    @NotNull
    GenericAttributeValue<Boolean> getSaveParameters();

}
