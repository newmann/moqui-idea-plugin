package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Cluster extends DomElement {
    
    public static final String TAG_NAME = "cluster";

    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getUrl();
    @NotNull GenericAttributeValue<String> getUser();
    @NotNull GenericAttributeValue<String> getPassword();
    @NotNull GenericAttributeValue<String> getIndexPrefix();
    @NotNull GenericAttributeValue<String> getPoolMax();
    @NotNull GenericAttributeValue<String> getQueueSize();

}
