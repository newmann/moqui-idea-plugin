package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

import java.util.List;

public interface RootScreen extends AbstractLocation {
    public static final String TAG_NAME = "root-screen";

    @NotNull
    GenericAttributeValue<String> getHost();
//    @NotNull
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<Boolean> getLocation();


}
