package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IfOtherOperationsList extends DomElement {
    @NotNull
    @SubTagList(Assert.TAG_NAME)
    List<Assert> getAssertList();

    @NotNull
    @SubTagList(If.TAG_NAME)
    List<If> getIfList();
    @NotNull
    @SubTagList(While.TAG_NAME)
    List<While> getWhileList();
}
