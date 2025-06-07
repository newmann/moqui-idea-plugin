package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.DisplayFormatConverter;
import org.moqui.idea.plugin.dom.converter.TextTemplateConverter;

import java.util.List;

public interface Display extends DomElement {
    
    public static final String TAG_NAME = "display";

    @NotNull
    @SubTagList(DependsOn.TAG_NAME)
    List<DependsOn> getDependsOnList();

    @NotNull GenericAttributeValue<Boolean> getAlsoHidden();
    @NotNull
    @Convert(TextTemplateConverter.class)
    GenericAttributeValue<String> getText();
    @NotNull GenericAttributeValue<String> getTextMap();
    @NotNull GenericAttributeValue<String> getStyle();
    @NotNull GenericAttributeValue<String> getCurrencyUnitField();
    @NotNull
    @Convert(DisplayFormatConverter.class)
    GenericAttributeValue<String> getFormat();
    @NotNull GenericAttributeValue<String> getTextFormat();
    @NotNull GenericAttributeValue<String> getEncode();
    @NotNull GenericAttributeValue<String> getDynamicTransition();
    @NotNull GenericAttributeValue<String> getParameterMap();
    @NotNull GenericAttributeValue<Boolean> getDependsOptional();
}
