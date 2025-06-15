package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TextLine extends DomElement {

    public static final String TAG_NAME = "text-line";

    @NotNull
    @SubTagList(DependsOn.TAG_NAME)
    List<DependsOn> getDependsOnList();

    @NotNull GenericAttributeValue<String> getSize();
    @NotNull GenericAttributeValue<String> getMaxLength();
    @NotNull GenericAttributeValue<String> getDefaultValue();
    @NotNull GenericAttributeValue<String> getFormat();
    @NotNull GenericAttributeValue<String> getMask();
    @NotNull GenericAttributeValue<String> getInputType();
    @NotNull GenericAttributeValue<String> getPrefix();
    @NotNull GenericAttributeValue<String> getAcTransition();
    @NotNull GenericAttributeValue<String> getAcDelay();
    @NotNull GenericAttributeValue<String> getAcMinLength();
    @NotNull GenericAttributeValue<String> getAcShowValue();
    @NotNull GenericAttributeValue<String> getAcInitialText();
    @NotNull GenericAttributeValue<String> getAcUseActual();
    @NotNull GenericAttributeValue<String> getDefaultTransition();
    @NotNull GenericAttributeValue<String> getParameterMap();


}
