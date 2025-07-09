package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ResourceReference extends DomElement {

    String TAG_NAME = "resource-reference";


    String ATTR_CLASS = "class";

    @NotNull GenericAttributeValue<String> getScheme();

    @NotNull
    @Attribute(ATTR_CLASS)
    GenericAttributeValue<String> getClassAttr();

}
