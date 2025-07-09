package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface LimitRange extends DomElement {

    String TAG_NAME = "limit-range";

    @NotNull
    GenericAttributeValue<String> getStart();

    @NotNull
    GenericAttributeValue<String> getSize();

}
