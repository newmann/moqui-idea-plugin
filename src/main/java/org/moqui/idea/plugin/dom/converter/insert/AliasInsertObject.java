package org.moqui.idea.plugin.dom.converter.insert;

import org.jetbrains.annotations.NotNull;

public class AliasInsertObject {
    public static AliasInsertObject of(@NotNull String entityAlias){
        return  new AliasInsertObject(entityAlias);
    }
    AliasInsertObject(@NotNull String entityAlias){
        this.entityAlias = entityAlias;
    }
    private String entityAlias;

    public String getEntityAlias() {
        return entityAlias;
    }

    public void setEntityAlias(String entityAlias) {
        this.entityAlias = entityAlias;
    }
}
