package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface TransactionJndi extends DomElement {
    
    public static final String TAG_NAME = "transaction-jndi";

    @NotNull GenericAttributeValue<String> getUserTransactionJndiName();
    @NotNull GenericAttributeValue<String> getTransactionManagerJndiName();

}
