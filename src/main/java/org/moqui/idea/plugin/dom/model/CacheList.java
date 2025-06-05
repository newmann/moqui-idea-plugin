package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CacheList extends DomElement {
    
    public static final String TAG_NAME = "cache-list";

    @NotNull
    GenericAttributeValue<Boolean> getWarmOnStart();

    @NotNull
    GenericAttributeValue<String> getLocalFactory();

    @NotNull
    GenericAttributeValue<String> getDistributedFactory();

    @NotNull
    @SubTagList(Cache.TAG_NAME)
    List<Cache> getCacheList();

}
