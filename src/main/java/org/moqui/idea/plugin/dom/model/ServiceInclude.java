package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.ServiceIncludeReferenceConverter;

public interface ServiceInclude extends AbstractLocation {
    
    String TAG_NAME = "service-include";
    
    String ATTR_VERB = "verb";
    
    String ATTR_NOUN = "noun";
    
//    public static final String ATTR_LOCATION = "location";
    @NotNull
    @Attribute(ATTR_VERB)
    @Referencing(ServiceIncludeReferenceConverter.class)
    GenericAttributeValue<String> getVerb();

    @NotNull
    @Attribute(ATTR_NOUN)
    @Referencing(ServiceIncludeReferenceConverter.class)
    GenericAttributeValue<String> getNoun();

//    @NotNull
//    @Attribute(ATTR_LOCATION)
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

}
