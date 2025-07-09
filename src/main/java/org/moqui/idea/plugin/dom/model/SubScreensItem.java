package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface SubScreensItem extends AbstractLocation {
    
    String TAG_NAME = "subscreens-item";
//    String ATTR_LOCATION = "location";
    String ATTR_NAME = "name";
//    @NotNull
//    @SubTagList(ConditionalDefault.TAG_NAME)
//    List<ConditionalDefault> getConditionalDefaultList();
    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();

//    @NotNull
//    @Attribute(ATTR_LOCATION)
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

    @NotNull GenericAttributeValue<String> getMenuTitle();
    @NotNull GenericAttributeValue<String> getMenuIndex();
    @NotNull GenericAttributeValue<Boolean> getMenuInclude();
    @NotNull GenericAttributeValue<Boolean> getNoSubPath();
}
