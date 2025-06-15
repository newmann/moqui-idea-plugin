package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface TextLength extends DomElement {

    public static final String TAG_NAME = "text-length";

    @NotNull GenericAttributeValue<String> getMin();
    @NotNull GenericAttributeValue<String> getMax();
}
