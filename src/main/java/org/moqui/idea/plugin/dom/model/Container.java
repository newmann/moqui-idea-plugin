package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Container extends WidgetElementsList {
    
    String TAG_NAME = "container";

//    @NotNull
//    @SubTagList(EntityOptions.TAG_NAME)
//    List<Set> getSetList();

//    @NotNull GenericAttributeValue<String> getLocation();
    @NotNull GenericAttributeValue<String> getId();
    @NotNull GenericAttributeValue<String> getStyle();
    @NotNull GenericAttributeValue<String> getType();

}
