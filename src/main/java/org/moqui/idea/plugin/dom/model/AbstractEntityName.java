package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityAndViewNameReferenceConverter;

public interface AbstractEntityName extends DomElement {
   String ATTR_ENTITY_NAME = "entity-name";
    @NotNull
    @Attribute(ATTR_ENTITY_NAME)
//    @Convert(EntityFullNameConverter.class)
    @Referencing(EntityAndViewNameReferenceConverter.class)
    GenericAttributeValue<String> getEntityName();


}
