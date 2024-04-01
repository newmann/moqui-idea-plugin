package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

public interface AbstractTransition extends DomElement {
    public static final String ATTR_NAME = "name";

    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();



}
