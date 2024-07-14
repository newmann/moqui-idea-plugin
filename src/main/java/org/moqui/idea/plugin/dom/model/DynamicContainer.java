package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.TransitionConverter;

import java.util.List;

public interface DynamicContainer extends DomElement {
    public static final String TAG_NAME = "dynamic-container";

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull GenericAttributeValue<String> getId();

    @NotNull
    @Convert(TransitionConverter.class)
    GenericAttributeValue<String> getTransition();

    @NotNull GenericAttributeValue<String> getParameterMap();
}
