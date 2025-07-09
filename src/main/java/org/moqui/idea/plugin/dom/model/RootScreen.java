package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface RootScreen extends AbstractLocation {

    String TAG_NAME = "root-screen";

    @NotNull
    GenericAttributeValue<String> getHost();
//    @NotNull
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<Boolean> getLocation();


}
