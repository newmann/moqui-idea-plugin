package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Password extends DomElement {
    String TAG_NAME = "password";

    @NotNull GenericAttributeValue<String> getSize();
    @NotNull GenericAttributeValue<String> getMaxlength();

    //Formoqui-conf-3.xsd

    @NotNull GenericAttributeValue<String> getEncryptHashType();
    @NotNull GenericAttributeValue<String> getMinLength();
    @NotNull GenericAttributeValue<String> getMinDigits();
    @NotNull GenericAttributeValue<String> getMinOthers();
    @NotNull GenericAttributeValue<String> getHistoryLimit();
    @NotNull GenericAttributeValue<String> getChangeWeeks();
    @NotNull GenericAttributeValue<String> getEmailRequireChange();
    @NotNull GenericAttributeValue<String> getEmailExpireHours();

}
