package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface DatabaseType extends DomElement {

    public static final String TAG_NAME = "database-type";

    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getSqlType();
    @NotNull GenericAttributeValue<String> getSqlTypeAlias();
    @NotNull GenericAttributeValue<String> getJavaType();

}
