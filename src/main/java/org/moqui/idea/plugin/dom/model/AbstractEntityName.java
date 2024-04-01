package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFullNameConverter;

public interface AbstractEntityName extends DomElement {
    public static final String ATTR_ENTITY_NAME = "entity-name";
    @NotNull
    @Attribute(ATTR_ENTITY_NAME)
    @Convert(EntityFullNameConverter.class)
    GenericAttributeValue<String> getEntityName();


}
