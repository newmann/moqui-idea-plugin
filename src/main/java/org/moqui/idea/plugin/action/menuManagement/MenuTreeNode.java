package org.moqui.idea.plugin.action.menuManagement;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MoquiUrl;

public class MenuTreeNode {
    private final MoquiUrl menu;
    private final String name;


    MenuTreeNode(@NotNull MoquiUrl menu) {
        this.menu = menu;
        this.name = menu.getTitle();

    }

    public String toString(){
        return this.name ;    }

    public MoquiUrl getMenu(){
        return this.menu;
    }

}
