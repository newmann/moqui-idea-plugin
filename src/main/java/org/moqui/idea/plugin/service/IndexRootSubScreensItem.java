package org.moqui.idea.plugin.service;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.SubScreensItem;
import org.moqui.idea.plugin.util.MyDomUtils;

public final class IndexRootSubScreensItem extends AbstractIndex {

    final private SubScreensItem subScreensItem;


    final private String name;
    final private String location;


    IndexRootSubScreensItem(@NotNull SubScreensItem subScreensItem){
        this.subScreensItem =subScreensItem;
        this.name = MyDomUtils.getValueOrEmptyString(subScreensItem.getName());
        this.location = MyDomUtils.getValueOrEmptyString(subScreensItem.getLocation());

        this.setLastRefreshStamp(System.currentTimeMillis());

    }

    public SubScreensItem getSubScreensItem(){return this.subScreensItem;}

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
