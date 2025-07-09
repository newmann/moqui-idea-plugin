package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Columns extends DomElement {
    
    String TAG_NAME = "columns";

    String ATTR_TYPE="type";
    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();
}
