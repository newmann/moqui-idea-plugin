package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ArtifactStats extends DomElement {
    
    String TAG_NAME = "artifact-stats";

    @NotNull
    GenericAttributeValue<String> getType();
    @NotNull
    GenericAttributeValue<Boolean> getPersistHit();
    @NotNull
    GenericAttributeValue<Boolean> getPersistBin();


}
