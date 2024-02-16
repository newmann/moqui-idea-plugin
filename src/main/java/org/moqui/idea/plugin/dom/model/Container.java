package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Container extends WidgetElementsList {
    public static final String TAG_NAME = "container";

//    @NotNull
//    @SubTagList(EntityOptions.TAG_NAME)
//    List<Set> getSetList();

//    @NotNull GenericAttributeValue<String> getLocation();
    @NotNull GenericAttributeValue<String> getId();
    @NotNull GenericAttributeValue<String> getStyle();
    @NotNull GenericAttributeValue<String> getType();

}
