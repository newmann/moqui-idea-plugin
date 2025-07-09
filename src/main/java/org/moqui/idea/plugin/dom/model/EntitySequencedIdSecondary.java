package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface EntitySequencedIdSecondary extends DomElement {
    
    String TAG_NAME = "entity-sequenced-id-secondary";

    @NotNull GenericAttributeValue<String> getValueField();


}
