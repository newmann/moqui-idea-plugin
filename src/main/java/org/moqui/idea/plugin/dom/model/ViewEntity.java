package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ViewEntity extends DomElement {
    public static final String TAG_NAME = "view-entity";
    @NotNull
    @Attribute("entity-name")
    GenericAttributeValue<String> getEntityName();

    @NotNull
    @Attribute("package")
    GenericAttributeValue<String> getPackage();

}
