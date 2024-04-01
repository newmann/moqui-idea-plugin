package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

public interface ComponentDir extends DomElement {
    public static final String TAG_NAME = "component-dir";

    @NotNull
    //@Convert(LocationConverter.class)
    GenericAttributeValue<String>
    getLocation();


}
