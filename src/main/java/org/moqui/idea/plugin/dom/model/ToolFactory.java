package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ToolFactory extends DomElement {
    public static final String TAG_NAME = "tool-factory";
    public static final String ATTR_CLASS = "class";
    @NotNull
    @Attribute(ATTR_CLASS)
    GenericAttributeValue<String> getClassAttr();

    @NotNull
    GenericAttributeValue<Integer> getInitPriority();
    @NotNull
    GenericAttributeValue<Boolean> getDisabled();

}
