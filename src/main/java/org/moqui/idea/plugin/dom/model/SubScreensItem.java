package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

import java.util.List;

public interface SubScreensItem extends AbstractLocation {
    public static final String TAG_NAME = "subscreens-item";
//    public static final String ATTR_LOCATION = "location";
    public static final String ATTR_NAME = "name";
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
