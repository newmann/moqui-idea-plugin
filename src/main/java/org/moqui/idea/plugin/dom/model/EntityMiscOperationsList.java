package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EntityMiscOperationsList extends DomElement {
    @NotNull
    @SubTagList(EntityData.TAG_NAME)
    List<EntityData> getEntityDataList();

    @NotNull
    @SubTagList(EntitySequencedIdPrimary.TAG_NAME)
    List<EntitySequencedIdPrimary> getEntitySequencedIdPrimaryList();
    @NotNull
    @SubTagList(EntitySequencedIdSecondary.TAG_NAME)
    List<EntitySequencedIdSecondary> getEntitySequencedIdSecondaryList();

}
