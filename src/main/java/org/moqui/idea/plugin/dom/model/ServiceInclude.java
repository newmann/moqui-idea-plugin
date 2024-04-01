package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

public interface ServiceInclude extends AbstractLocation {
    public static final String TAG_NAME = "service-include";
    public static final String ATTR_VERB = "verb";
    public static final String ATTR_NOUN = "noun";
    public static final String ATTR_LOCATION = "location";
    @NotNull
    @Attribute(ATTR_VERB)
    GenericAttributeValue<String> getVerb();

    @NotNull
    @Attribute(ATTR_NOUN)
    GenericAttributeValue<String> getNoun();

//    @NotNull
//    @Attribute(ATTR_LOCATION)
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

}
