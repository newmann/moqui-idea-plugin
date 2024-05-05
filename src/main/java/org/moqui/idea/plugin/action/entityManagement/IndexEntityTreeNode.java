package org.moqui.idea.plugin.action.entityManagement;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractEntity;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.MyDomUtils;

public class IndexEntityTreeNode {
    private final IndexEntity indexEntity;
    private final String name;


    IndexEntityTreeNode(@NotNull IndexEntity indexEntity) {
        this.indexEntity = indexEntity;
        this.name = indexEntity.getEntityName();

    }

    public String toString(){
        return this.name ;    }

    public IndexEntity getIndexEntity(){
        return this.indexEntity;
    }

}
