package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

public interface Text extends AbstractLocation {
    public static final String TAG_NAME = "text";

    @NotNull GenericAttributeValue<String> getType();

//    @NotNull
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

    @NotNull GenericAttributeValue<String> getTemplate();
    @NotNull GenericAttributeValue<String> getEncode();
    @NotNull GenericAttributeValue<String> getNoBoundaryComment();
}
