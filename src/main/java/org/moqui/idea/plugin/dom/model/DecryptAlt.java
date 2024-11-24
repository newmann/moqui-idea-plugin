package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface DecryptAlt extends DomElement {
    public static final String TAG_NAME = "decrypt-alt";

    @NotNull GenericAttributeValue<String> getCryptPass();
    @NotNull GenericAttributeValue<String> getCryptSalt();
    @NotNull GenericAttributeValue<String> getCryptIter();
    @NotNull GenericAttributeValue<String> getCryptAlgo();


}
