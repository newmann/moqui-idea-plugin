package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Servlet extends DomElement {
    
    public static final String TAG_NAME = "servlet";
    
    public static final String ATTR_CLASS = "class";
    @NotNull GenericAttributeValue<String> getName();

    @NotNull
    @Attribute(ATTR_CLASS)
    GenericAttributeValue<String> getClassAttr();
    @NotNull GenericAttributeValue<String> getLoadOnStartup();
    @NotNull GenericAttributeValue<Boolean> getAsyncSupported();
    @NotNull GenericAttributeValue<Boolean> getEnabled();

    @NotNull
    @SubTagList(InitParam.TAG_NAME)
    List<InitParam> getInitParamList();
    @NotNull
    @SubTagList(UrlPattern.TAG_NAME)
    List<UrlPattern> getUrlPatternList();

}
