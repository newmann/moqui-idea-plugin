package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

import java.util.List;

public interface WidgetTemplateInclude extends AbstractLocation {
    public static final String TAG_NAME = "widget-template-include";
//    public static final String ATTR_LOCATION = "location";


    @NotNull
    @SubTagList(Set.TAG_NAME)
    List<Set> getSetList();

//    @NotNull
//    @Attribute(ATTR_LOCATION)
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

}
