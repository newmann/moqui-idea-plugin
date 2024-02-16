package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface EnvOperations extends DomElement {
    @NotNull
    @SubTag(Set.TAG_NAME)
    Set getSet();

    @NotNull
    @SubTag(OrderMapList.TAG_NAME)
    OrderMapList getOrderMapList();
    @NotNull
    @SubTag(FilterMapList.TAG_NAME)
    FilterMapList getFilterMapList();

}
