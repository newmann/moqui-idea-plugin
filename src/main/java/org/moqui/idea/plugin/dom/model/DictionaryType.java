package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface DictionaryType extends DomElement {

    String TAG_NAME = "dictionary-type";

    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getDefaultSqlType();
    @NotNull GenericAttributeValue<String> getJavaType();

}
