package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

public interface LoadData extends DomElement {
    public static final String TAG_NAME = "load-data";

    @NotNull
    @Convert(LocationConverter.class)
    GenericAttributeValue<String> getLocation();

}
