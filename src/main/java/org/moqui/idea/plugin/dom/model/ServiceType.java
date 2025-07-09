package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ServiceType extends DomElement {

    String TAG_NAME = "service-type";

    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getRunnerClass();


}
