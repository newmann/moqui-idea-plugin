package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.AllPackageConverter;

public interface AbstractEntity extends DomElement {
    public static final String ATTR_ENTITY_NAME = "entity-name";
    public static final String ATTR_PACKAGE = "package";

    @NotNull
    @Attribute(ATTR_ENTITY_NAME)
    GenericAttributeValue<String> getEntityName();

    @NotNull
    @Attribute(ATTR_PACKAGE)
    @Convert(AllPackageConverter.class)
    GenericAttributeValue<String> getPackage();

}
