package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.TransitionIncludeNameReferenceConverter;

public interface AbstractTransition extends DomElement {
    String ATTR_NAME = "name";

    @NotNull
    @NameValue
    @Attribute(ATTR_NAME)
    @Referencing(TransitionIncludeNameReferenceConverter.class)
    GenericAttributeValue<String> getName();



}
