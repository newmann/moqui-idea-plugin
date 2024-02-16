package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface EConditionObject extends DomElement {
    public static final String TAG_NAME = "econdition-object";

    @NotNull GenericAttributeValue<String> getField();

}
