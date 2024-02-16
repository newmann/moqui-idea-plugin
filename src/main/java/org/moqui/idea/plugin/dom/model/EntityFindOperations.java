package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface EntityFindOperations extends DomElement {
    @NotNull
    @SubTag(EntityFindOne.TAG_NAME)
    EntityFindOne getEntityFindOne();

    @NotNull
    @SubTag(EntityFind.TAG_NAME)
    EntityFind getEntityFind();
    @NotNull
    @SubTag(EntityFindCount.TAG_NAME)
    EntityFindCount getEntityFindCount();
    @NotNull
    @SubTag(EntityFindRelatedOne.TAG_NAME)
    EntityFindRelatedOne getEntityFindRelatedOne();
    @NotNull
    @SubTag(EntityFindRelated.TAG_NAME)
    EntityFindRelated getEntityFindRelated();
}
