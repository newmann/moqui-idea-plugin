package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface ContainerBox extends WidgetElementsList {

    public static final String TAG_NAME = "container-box";

    @NotNull
    @SubTag(BoxHeader.TAG_NAME)
    BoxHeader getBoxHeader();
    @NotNull
    @SubTag(BoxToolbar.TAG_NAME)
    BoxToolbar getBoxToolbar();
    @NotNull
    @SubTag(BoxBody.TAG_NAME)
    BoxBody getBoxBody();
    @NotNull
    @SubTag(BoxBodyNopad.TAG_NAME)
    BoxBodyNopad getBoxBodyNopad();
//    @NotNull
//    @SubTagList(EntityOptions.TAG_NAME)
//    List<Set> getSetList();

//    @NotNull GenericAttributeValue<String> getLocation();
    @NotNull GenericAttributeValue<String> getId();
    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getInitial();


}
