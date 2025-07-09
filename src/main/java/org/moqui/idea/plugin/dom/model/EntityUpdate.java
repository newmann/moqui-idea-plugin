package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface EntityUpdate extends DomElement {

    String TAG_NAME = "entity-update";

    String ATTR_VALUE_FIELD = "value-field";

    @NotNull
    @Attribute(ATTR_VALUE_FIELD)
    GenericAttributeValue<String> getValueField();


}
