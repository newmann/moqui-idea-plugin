package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ConditionalDefault extends DomElement {
    
    String TAG_NAME = "conditional-default";

    @NotNull GenericAttributeValue<String> getCondition();

    @NotNull GenericAttributeValue<String> getItem();
}
