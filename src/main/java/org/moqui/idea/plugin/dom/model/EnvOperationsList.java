package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EnvOperationsList extends DomElement {
    @NotNull
    @SubTagList(Set.TAG_NAME)
    List<Set> getSetList();

    @NotNull
    @SubTagList(OrderMapList.TAG_NAME)
    List<OrderMapList> getOrderMapListList();
    @NotNull
    @SubTagList(FilterMapList.TAG_NAME)
    List<FilterMapList> getFilterMapListList();

}
