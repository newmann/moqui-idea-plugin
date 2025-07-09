package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ArtifactExecutionFacade extends DomElement {
    
    String TAG_NAME = "artifact-execution-facade";


    @NotNull
    @SubTagList(ArtifactExecution.TAG_NAME)
    List<ArtifactExecution> getArtifactExecutionList();


}
