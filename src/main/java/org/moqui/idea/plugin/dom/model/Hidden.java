package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Hidden extends DomElement {
    
    String TAG_NAME ="hidden";

    @NotNull GenericAttributeValue<String> getDefaultView();


}
