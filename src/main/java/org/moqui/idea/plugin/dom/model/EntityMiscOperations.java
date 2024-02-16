package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface EntityMiscOperations extends DomElement {
    @NotNull
    @SubTag(EntityData.TAG_NAME)
    EntityData getEntityData();

    @NotNull
    @SubTag(EntitySequencedIdPrimary.TAG_NAME)
    EntitySequencedIdPrimary getEntitySequencedIdPrimary();
    @NotNull
    @SubTag(EntitySequencedIdSecondary.TAG_NAME)
    EntitySequencedIdSecondary getEntitySequencedIdSecondary();

}
