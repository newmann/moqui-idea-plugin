package org.moqui.idea.plugin.action.menuManagement;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.ScreenUtils;

public class MenuTreeNode {
    private final ScreenUtils.Menu menu;
    private final String name;


    MenuTreeNode(@NotNull ScreenUtils.Menu menu) {
        this.menu = menu;
        this.name = menu.getTitle();

    }

    public String toString(){
        return this.name ;    }

    public ScreenUtils.Menu getMenu(){
        return this.menu;
    }

}
