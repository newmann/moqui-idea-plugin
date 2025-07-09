package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Listener extends DomElement {
    
    String TAG_NAME = "listener";
    
    String ATTR_CLASS = "class";
    @NotNull
    @Attribute(ATTR_CLASS)
    GenericAttributeValue<String> getClassAttr();

    @NotNull
    GenericAttributeValue<Boolean> getEnabled();

}
