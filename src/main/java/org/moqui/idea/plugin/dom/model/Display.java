package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Display extends DomElement {
    public static final String TAG_NAME = "display";

    @NotNull
    @SubTagList(DependsOn.TAG_NAME)
    List<DependsOn> getDependsOnList();

    @NotNull GenericAttributeValue<Boolean> getAlsoHidden();
    @NotNull GenericAttributeValue<String> getText();
    @NotNull GenericAttributeValue<String> getTextMap();
    @NotNull GenericAttributeValue<String> getStyle();
    @NotNull GenericAttributeValue<String> getCurrencyUnitField();
    @NotNull GenericAttributeValue<String> getFormat();
    @NotNull GenericAttributeValue<String> getTextFormat();
    @NotNull GenericAttributeValue<String> getEncode();
    @NotNull GenericAttributeValue<String> getDynamicTransition();
    @NotNull GenericAttributeValue<String> getParameterMap();
    @NotNull GenericAttributeValue<Boolean> getDependsOptional();
}
