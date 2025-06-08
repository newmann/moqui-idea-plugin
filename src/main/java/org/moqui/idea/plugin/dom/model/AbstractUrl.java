package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.TransitionIncludeNameConverter;
import org.moqui.idea.plugin.dom.converter.UrlConverter;

public interface AbstractUrl extends DomElement {
    public static final String ATTR_URL = "url";

    @NotNull
    @Attribute(ATTR_URL)
    @Convert(UrlConverter.class)
    GenericAttributeValue<String> getUrl();



}
