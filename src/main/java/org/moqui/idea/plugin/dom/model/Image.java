package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Image extends AbstractUrl {
    String TAG_NAME = "image";
//
    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull GenericAttributeValue<String> getId();
//    @NotNull GenericAttributeValue<String> getUrl();
    @NotNull GenericAttributeValue<String> getUrlType();
    @NotNull GenericAttributeValue<String> getWidth();
    @NotNull GenericAttributeValue<String> getHeight();
    @NotNull GenericAttributeValue<String> getAlt();
    @NotNull GenericAttributeValue<String> getStyle();
    @NotNull GenericAttributeValue<String> getHover();
    @NotNull GenericAttributeValue<String> getParameterMap();
    @NotNull GenericAttributeValue<String> getCondition();


}
