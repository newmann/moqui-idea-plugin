package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface AttListResponse extends DomElement {




    @NotNull
    GenericAttributeValue<String> getType();

    @NotNull
    GenericAttributeValue<String> getUrl();

    @NotNull
    GenericAttributeValue<String> getUrlType();
    @NotNull
    GenericAttributeValue<String> getParameterMap();
    @NotNull
    GenericAttributeValue<Boolean> getSaveCurrentScreen();
    @NotNull
    GenericAttributeValue<Boolean> getSaveParameters();

}
