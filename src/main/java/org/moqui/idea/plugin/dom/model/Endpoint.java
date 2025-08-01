package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Endpoint extends DomElement {
    
    String TAG_NAME = "endpoint";
    
    String ATTR_CLASS = "class";
    @NotNull GenericAttributeValue<String> getPath();

    @NotNull
    @Attribute(ATTR_CLASS)
    GenericAttributeValue<String> getClassAttr();
    @NotNull GenericAttributeValue<String> getTimeout();
    @NotNull GenericAttributeValue<Boolean> getEnabled();



}
