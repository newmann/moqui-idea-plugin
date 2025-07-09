package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface SessionConfig extends DomElement {
    
    String TAG_NAME = "session-config";


    @NotNull
    GenericAttributeValue<String> getTimeout();

}
