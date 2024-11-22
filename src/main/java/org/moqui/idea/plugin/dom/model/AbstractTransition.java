package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.TransitionIncludeNameConverter;

public interface AbstractTransition extends DomElement {
    public static final String ATTR_NAME = "name";

    @NotNull
    @NameValue
    @Attribute(ATTR_NAME)
    @Convert(TransitionIncludeNameConverter.class)
    GenericAttributeValue<String> getName();



}
