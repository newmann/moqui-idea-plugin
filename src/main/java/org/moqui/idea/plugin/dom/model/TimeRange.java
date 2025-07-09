package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface TimeRange extends DomElement {
    
    String TAG_NAME = "time-range";

    @NotNull GenericAttributeValue<String> getAfter();
    @NotNull GenericAttributeValue<String> getBefore();
    @NotNull GenericAttributeValue<String> getFormat();

}
