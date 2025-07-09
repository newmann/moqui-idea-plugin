package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface LoginKey extends DomElement {

    String TAG_NAME = "login-key";

    @NotNull GenericAttributeValue<String> getEncryptHashType();
    @NotNull GenericAttributeValue<String> getExpireHours();

}
