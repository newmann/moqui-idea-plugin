package org.moqui.idea.plugin.action.entityManagement;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.IndexViewEntity;

public class IndexViewEntityTreeNode {
    private final IndexViewEntity indexViewEntity;
    private final String name;


    IndexViewEntityTreeNode(@NotNull IndexViewEntity indexViewEntity) {
        this.indexViewEntity = indexViewEntity;
        this.name = indexViewEntity.getShortName();

    }

    public String toString(){
        return this.name ;    }

    public IndexViewEntity getIndexViewEntity(){
        return this.indexViewEntity;
    }

}
