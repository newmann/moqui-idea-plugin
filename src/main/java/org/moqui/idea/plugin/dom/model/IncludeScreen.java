package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface IncludeScreen extends AbstractLocation {
    
    public static final String TAG_NAME = "include-screen";

//    @NotNull
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

    @NotNull GenericAttributeValue<String> getShareScope();

}
