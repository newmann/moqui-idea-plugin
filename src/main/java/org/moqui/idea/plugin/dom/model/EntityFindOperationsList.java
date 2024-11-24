package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EntityFindOperationsList extends DomElement {
    @NotNull
    @SubTagList(EntityFindOne.TAG_NAME)
    List<EntityFindOne> getEntityFindOneList();

    @NotNull
    @SubTagList(EntityFind.TAG_NAME)
    List<EntityFind> getEntityFindList();
    @NotNull
    @SubTagList(EntityFindCount.TAG_NAME)
    List<EntityFindCount> getEntityFindCountList();
    @NotNull
    @SubTagList(EntityFindRelatedOne.TAG_NAME)
    List<EntityFindRelatedOne> getEntityFindRelatedOneList();
    @NotNull
    @SubTagList(EntityFindRelated.TAG_NAME)
    List<EntityFindRelated> getEntityFindRelatedList();
}
