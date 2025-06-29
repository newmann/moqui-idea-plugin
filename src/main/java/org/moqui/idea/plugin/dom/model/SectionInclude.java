package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.SectionIncludeNameReferenceConverter;

public interface SectionInclude extends AbstractLocation {
    
    String TAG_NAME = "section-include";
    String ATTR_NAME = "name";
    @NotNull
    @Referencing(SectionIncludeNameReferenceConverter.class)
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();

//    @NotNull
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

}
