package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ServerStats extends DomElement {
    
    String TAG_NAME = "server-stats";

    @NotNull
    GenericAttributeValue<String> getBinLengthSeconds();
    @NotNull
    GenericAttributeValue<Boolean> getVisitEnabled();
    @NotNull
    GenericAttributeValue<Boolean> getVisitIpInfoOnLogin();
    @NotNull
    GenericAttributeValue<Boolean> getVisitorEnabled();
    @NotNull
    GenericAttributeValue<Boolean> getStatsSkipCondition();

    @NotNull
    @SubTagList(ArtifactStats.TAG_NAME)
    List<ArtifactStats> getArtifactStatsList();

}
