package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IfCombineOperationsList extends DomElement {
    @NotNull
    @SubTagList(Or.TAG_NAME)
    List<Or> getOrList();

    @NotNull
    @SubTagList(And.TAG_NAME)
    List<And> getAndList();
    @NotNull
    @SubTagList(Not.TAG_NAME)
    List<Not> getNotList();

}
