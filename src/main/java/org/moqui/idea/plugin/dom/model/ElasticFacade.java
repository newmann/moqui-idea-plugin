package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElasticFacade extends DomElement {
    
    public static final String TAG_NAME = "elastic-facade";

    @NotNull
    @SubTagList(Cluster.TAG_NAME)
    List<Cluster> getClusterList();

}
