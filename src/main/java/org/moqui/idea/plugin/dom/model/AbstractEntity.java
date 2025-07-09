package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.AllPackageConverter;

import java.util.List;

public interface AbstractEntity extends DomElement {
    String ATTR_ENTITY_NAME = "entity-name";
    String ATTR_PACKAGE = "package";

    //@Stubbed
    @NameValue
    @NotNull
    @Attribute(ATTR_ENTITY_NAME)
    GenericAttributeValue<String> getEntityName();

    @NotNull
    @Attribute(ATTR_PACKAGE)
    @Convert(AllPackageConverter.class)
    GenericAttributeValue<String> getPackage();

    @NotNull
    @SubTagList(Relationship.TAG_NAME)
    List<Relationship> getRelationshipList();

    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();
}
