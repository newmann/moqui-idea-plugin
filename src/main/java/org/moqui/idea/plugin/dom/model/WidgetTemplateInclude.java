package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface WidgetTemplateInclude extends AbstractLocation {

    String TAG_NAME = "widget-template-include";
//    String ATTR_LOCATION = "location";


    @NotNull
    @SubTagList(Set.TAG_NAME)
    List<Set> getSetList();

//    @NotNull
//    @Attribute(ATTR_LOCATION)
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

}
