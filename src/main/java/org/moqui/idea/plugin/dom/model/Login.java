package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Login extends DomElement {
    public static final String TAG_NAME = "login";

    @NotNull GenericAttributeValue<String> getMaxFailures();
    @NotNull GenericAttributeValue<String> getDisableMinutes();
    @NotNull GenericAttributeValue<String> getHistoryStore();
    @NotNull GenericAttributeValue<String> getHistoryIncorrectPassword();
}
