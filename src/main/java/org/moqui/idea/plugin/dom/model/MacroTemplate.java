package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface MacroTemplate extends DomElement {
    
    String TAG_NAME = "macro-template";

    String ATTR_TYPE ="type";

    
    String ATTR_LOCATION ="location";

//    String ATTR_JOIN_FROM_ALIAS ="join-from-alias";

    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();

    @NotNull
    @Attribute(ATTR_LOCATION)
//    @Convert(LocationConverter.class)
    GenericAttributeValue<String> getLocation();

}
