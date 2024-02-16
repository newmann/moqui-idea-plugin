package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.TransitionConverter;

import java.util.List;

public interface DynamicOptions extends DomElement {
    public static final String TAG_NAME = "dynamic-options";

    @NotNull
    @SubTagList(DependsOn.TAG_NAME)
    List<DependsOn> getDependsOnList();

    @NotNull
    @Convert(TransitionConverter.class)
    GenericAttributeValue<String> getTransition();

    @NotNull GenericAttributeValue<String> getValueField();
    @NotNull GenericAttributeValue<String> getLabelField();
    @NotNull GenericAttributeValue<Boolean> getDependsOptional();
    @NotNull GenericAttributeValue<Boolean> getServerSearch();

    @NotNull GenericAttributeValue<String> getDelay();
    @NotNull GenericAttributeValue<String> getMinLength();
    @NotNull GenericAttributeValue<String> getParameterMap();
}
