package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface WebSettings extends DomElement {
    
    public static final String TAG_NAME = "web-settings";

    @NotNull
    GenericAttributeValue<Boolean> getAllowWebRequest();
    @NotNull
    GenericAttributeValue<Boolean> getRequireEncryption();
    @NotNull
    GenericAttributeValue<String> getMimeType();
    @NotNull
    GenericAttributeValue<String> getCharacterEncoding();

}
