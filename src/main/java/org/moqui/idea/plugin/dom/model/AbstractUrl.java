package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;
import org.moqui.idea.plugin.dom.converter.LocationReferenceConverter;
import org.moqui.idea.plugin.dom.converter.UrlConverter;

public interface AbstractUrl extends DomElement {
    String ATTR_URL = "url";

    @NotNull
    @Attribute(ATTR_URL)
//    @Convert(LocationConverter.class)
    @Referencing(LocationReferenceConverter.class)
    GenericAttributeValue<String> getUrl();



}
