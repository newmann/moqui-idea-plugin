package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Matches extends DomElement {

    String TAG_NAME = "matches";

    @NotNull GenericAttributeValue<String> getRegexp();
    @NotNull GenericAttributeValue<String> getMessage();
}
