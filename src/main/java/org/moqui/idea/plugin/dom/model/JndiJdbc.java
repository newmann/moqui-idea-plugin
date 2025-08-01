package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface JndiJdbc extends DomElement {
    
    String TAG_NAME = "jndi-jdbc";



    @NotNull GenericAttributeValue<String> getJndiName();
    @NotNull GenericAttributeValue<String> getIsolationLevel();

}
