package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.TransitionReferenceConverter;

import java.util.List;

public interface DynamicContainer extends DomElement {
    
    public static final String TAG_NAME = "dynamic-container";

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull GenericAttributeValue<String> getId();

    @NotNull
    @Referencing(TransitionReferenceConverter.class)
    GenericAttributeValue<String> getTransition();

    @NotNull GenericAttributeValue<String> getParameterMap();
}
