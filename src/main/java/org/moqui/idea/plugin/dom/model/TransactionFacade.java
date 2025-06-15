package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface TransactionFacade extends DomElement {
    
    public static final String TAG_NAME = "transaction-facade";

    @NotNull GenericAttributeValue<Boolean> getUseTransactionCache();
    @NotNull GenericAttributeValue<Boolean> getUseConnectionStash();
    @NotNull GenericAttributeValue<String> getUseLockTrack();
    @NotNull GenericAttributeValue<String> getUseStatementTimeout();
    @NotNull
    @SubTag(ServerJndi.TAG_NAME)
    ServerJndi getServerJndi();

    @NotNull
    @SubTag(TransactionJndi.TAG_NAME)
    TransactionJndi getTransactionJndi();

    @NotNull
    @SubTag(TransactionInternal.TAG_NAME)
    TransactionInternal getTransactionInternal();

}
