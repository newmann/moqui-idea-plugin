package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ExtendEntity extends DomElement {
    public static final String TAG_NAME = "extend-entity";
    public static final String ATTR_NAME_ENTITY_NAME = "entity-name";
    public static final String ATTR_NAME_PACKAGE = "package";
    @NotNull
    @Attribute(ATTR_NAME_ENTITY_NAME)
    GenericAttributeValue<String> getEntityName();

    @NotNull
    @Attribute(ATTR_NAME_PACKAGE)
    GenericAttributeValue<String> getPackage();

    @NotNull
    @SubTag("field")
    List<Field> getFields();

    @NotNull
    @SubTag("relationship")
    List<Relationship> getRelationships();
}
