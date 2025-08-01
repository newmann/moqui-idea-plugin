package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

public interface ServiceLocation extends AbstractLocation {
    
    String TAG_NAME = "service-location";

    @NotNull GenericAttributeValue<String> getName();
//    @NotNull
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();



}
