package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Editable extends DomElement {
    
    public static final String TAG_NAME = "editable";
//
    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull
    @SubTag(EditableLoad.TAG_NAME)
    EditableLoad getEditableLoad();

    @NotNull GenericAttributeValue<String> getId();

    @NotNull GenericAttributeValue<String> getText();
    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getWidgetType();
    @NotNull GenericAttributeValue<Boolean> getEncode();
    @NotNull GenericAttributeValue<Boolean> getDisplayIfEmpty();
    @NotNull GenericAttributeValue<String> getTransition();

    @NotNull GenericAttributeValue<String> getParameterName();
    @NotNull GenericAttributeValue<String> getParameterMap();


}
