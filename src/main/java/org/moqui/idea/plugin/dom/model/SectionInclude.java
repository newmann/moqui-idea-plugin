package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

public interface SectionInclude extends AbstractLocation {
    public static final String TAG_NAME = "section-include";
    @NotNull
    GenericAttributeValue<String> getName();

//    @NotNull
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

}
