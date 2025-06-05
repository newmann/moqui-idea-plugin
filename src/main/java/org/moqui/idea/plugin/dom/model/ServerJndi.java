package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface ServerJndi extends DomElement {
    
    public static final String TAG_NAME = "server-jndi";

    @NotNull GenericAttributeValue<String> getContextProviderUrl();
    @NotNull GenericAttributeValue<String> getInitialContextFactory();
    @NotNull GenericAttributeValue<String> getUrlPkgPrefixes();
    @NotNull GenericAttributeValue<String> getSecurityPrincipal();
    @NotNull GenericAttributeValue<String> getSecurityCredentials();



}
