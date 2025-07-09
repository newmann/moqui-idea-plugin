package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;
import org.moqui.idea.plugin.dom.converter.LocationReferenceConverter;

public interface AbstractLocation extends DomElement {
    String ATTR_LOCATION = "location";

    @NotNull
    @Attribute(ATTR_LOCATION)
//    @Convert(LocationConverter.class)
    @Referencing(LocationReferenceConverter.class)
    GenericAttributeValue<String> getLocation();
}
