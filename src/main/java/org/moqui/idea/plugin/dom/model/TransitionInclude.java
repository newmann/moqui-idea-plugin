package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface TransitionInclude extends AbstractTransition,AbstractLocation {
    public static final String TAG_NAME = "transition-include";

//    public static final String ATTR_LOCATION = "location";
//    public static final String ATTR_NAME = "name";

//    @NotNull
//    @Attribute(ATTR_NAME)
//    GenericAttributeValue<String> getName();

//    @NotNull
//    @Attribute(ATTR_LOCATION)
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();


    @NotNull GenericAttributeValue<String> getMethod();
}
