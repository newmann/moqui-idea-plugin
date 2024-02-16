package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DynamicContainer extends DomElement {
    public static final String TAG_NAME = "dynamic-container";

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull GenericAttributeValue<String> getId();
    @NotNull GenericAttributeValue<String> getTransition();
    @NotNull GenericAttributeValue<String> getParameterMap();
}
