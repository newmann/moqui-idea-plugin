package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ArtifactExecution extends DomElement {
    
    String TAG_NAME = "artifact-execution";

    @NotNull
    GenericAttributeValue<String> getType();
    @NotNull
    GenericAttributeValue<Boolean> getAuthzEnabled();
    @NotNull
    GenericAttributeValue<Boolean> getTarpitEnabled();


}
