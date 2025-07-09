package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface NumberRange extends DomElement {
    
    String TAG_NAME = "number-range";

    @NotNull GenericAttributeValue<String> getMin();
    @NotNull GenericAttributeValue<String> getMinIncludeEquals();
    @NotNull GenericAttributeValue<String> getMax();
    @NotNull GenericAttributeValue<String> getMaxIncludeEquals();
    @NotNull GenericAttributeValue<String> getMessage();
}
