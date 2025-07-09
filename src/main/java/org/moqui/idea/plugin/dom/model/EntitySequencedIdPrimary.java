package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface EntitySequencedIdPrimary extends DomElement {

    String TAG_NAME = "entity-sequenced-id-primary";

    @NotNull GenericAttributeValue<String> getValueField();


}
