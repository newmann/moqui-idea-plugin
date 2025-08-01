package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface NameReplace extends DomElement {

    String TAG_NAME = "name-replace";

    @NotNull GenericAttributeValue<String> getOriginal();
    @NotNull GenericAttributeValue<String> getReplace();

}
