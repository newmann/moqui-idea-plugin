package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

public interface IncludeScreen extends AbstractLocation {
    public static final String TAG_NAME = "include-screen";

//    @NotNull
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

    @NotNull GenericAttributeValue<String> getShareScope();

}
