package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface EntityValueOperations extends DomElement {
    @NotNull
    @SubTag(EntityMakeValue.TAG_NAME)
    EntityMakeValue getEntityMakeValue();

    @NotNull
    @SubTag(EntityCreate.TAG_NAME)
    EntityCreate getEntityCreate();
    @NotNull
    @SubTag(EntityUpdate.TAG_NAME)
    EntityUpdate getEntityUpdate();
    @NotNull
    @SubTag(EntityDelete.TAG_NAME)
    EntityDelete getEntityDelete();
    @NotNull
    @SubTag(EntityDeleteRelated.TAG_NAME)
    EntityDeleteRelated getEntityDeleteRelated();

    @NotNull
    @SubTag(EntityDeleteByCondition.TAG_NAME)
    EntityDeleteByCondition getEntityDeleteByCondition();

    @NotNull
    @SubTag(EntitySet.TAG_NAME)
    EntitySet getEntitySet();

}
