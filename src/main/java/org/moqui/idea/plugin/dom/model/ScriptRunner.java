package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ScriptRunner extends DomElement {
    
    String TAG_NAME = "script-runner";

    
    String ATTR_CLASS = "class";

    @NotNull GenericAttributeValue<String> getExtension();

    @NotNull
    @Attribute(ATTR_CLASS)
    GenericAttributeValue<String> getClassAttr();


    @NotNull GenericAttributeValue<String> getEngine();
}
