package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Label extends DomElement {
    public static final String TAG_NAME = "label";
//
//    @NotNull
//    @SubTagList(Parameter.TAG_NAME)
//    List<Parameter> getParameterList();

    @NotNull GenericAttributeValue<String> getText();
    @NotNull GenericAttributeValue<String> getTextMap();
    @NotNull GenericAttributeValue<String> getType();

    @NotNull GenericAttributeValue<String> getId();
    @NotNull GenericAttributeValue<String> getEncode();

    @NotNull GenericAttributeValue<Boolean> getDisplayIfEmpty();
    @NotNull GenericAttributeValue<String> getStyle();
    @NotNull GenericAttributeValue<String> getCondition();

    @NotNull GenericAttributeValue<String> getTooltip();



}
