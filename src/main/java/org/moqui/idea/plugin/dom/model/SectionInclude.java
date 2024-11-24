package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.SectionIncludeNameReferenceConverter;

public interface SectionInclude extends AbstractLocation {
    public static final String TAG_NAME = "section-include";
    @NotNull
    @Referencing(SectionIncludeNameReferenceConverter.class)
    GenericAttributeValue<String> getName();

//    @NotNull
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

}
