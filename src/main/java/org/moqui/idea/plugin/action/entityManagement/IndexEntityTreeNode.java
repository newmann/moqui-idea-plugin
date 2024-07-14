package org.moqui.idea.plugin.action.entityManagement;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.IndexEntity;

public class IndexEntityTreeNode {
    private final IndexEntity indexEntity;
    private final String name;


    IndexEntityTreeNode(@NotNull IndexEntity indexEntity) {
        this.indexEntity = indexEntity;
        this.name = indexEntity.getShortName();

    }

    public String toString(){
        return this.name ;    }

    public IndexEntity getIndexEntity(){
        return this.indexEntity;
    }

}
