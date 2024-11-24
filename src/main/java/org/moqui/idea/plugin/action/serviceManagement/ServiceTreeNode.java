package org.moqui.idea.plugin.action.serviceManagement;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.IndexService;

public class ServiceTreeNode {
    private final IndexService indexService;
    private final String name;


    ServiceTreeNode(@NotNull IndexService indexService) {
        this.indexService = indexService;
        this.name = indexService.getFunctionName();

    }

    public String toString(){
        return this.name ;    }

    public IndexService getIndexService() {
        return indexService;
    }
}
