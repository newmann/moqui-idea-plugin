package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface JndiJdbc extends DomElement {
    public static final String TAG_NAME = "jndi-jdbc";



    @NotNull GenericAttributeValue<String> getJndiName();
    @NotNull GenericAttributeValue<String> getIsolationLevel();

}
