package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EntityValueOperationsList extends DomElement {
    @NotNull
    @SubTagList(EntityMakeValue.TAG_NAME)
    List<EntityMakeValue> getEntityMakeValueList();

    @NotNull
    @SubTagList(EntityCreate.TAG_NAME)
    List<EntityCreate> getEntityCreateList();
    @NotNull
    @SubTagList(EntityUpdate.TAG_NAME)
    List<EntityUpdate> getEntityUpdateList();
    @NotNull
    @SubTagList(EntityDelete.TAG_NAME)
    List<EntityDelete> getEntityDeleteList();
    @NotNull
    @SubTagList(EntityDeleteRelated.TAG_NAME)
    List<EntityDeleteRelated> getEntityDeleteRelatedList();

    @NotNull
    @SubTagList(EntityDeleteByCondition.TAG_NAME)
    List<EntityDeleteByCondition> getEntityDeleteByConditionList();

    @NotNull
    @SubTagList(EntitySet.TAG_NAME)
    List<EntitySet> getEntitySetList();

}
