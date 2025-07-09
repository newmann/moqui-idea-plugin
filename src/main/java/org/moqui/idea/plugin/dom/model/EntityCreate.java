package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface EntityCreate extends DomElement {

    String TAG_NAME = "entity-create";


    @NotNull GenericAttributeValue<String> getValueField();
    @NotNull GenericAttributeValue<Boolean> getOrUpdate();


}
