package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

public interface MacroTemplate extends DomElement {
    public static final String TAG_NAME = "macro-template";

    public static final String ATTR_TYPE ="type";

    public static final String ATTR_LOCATION ="location";

//    public static final String ATTR_JOIN_FROM_ALIAS ="join-from-alias";

    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();

    @NotNull
    @Attribute(ATTR_LOCATION)
//    @Convert(LocationConverter.class)
    GenericAttributeValue<String> getLocation();

}
