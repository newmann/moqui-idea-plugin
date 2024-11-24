package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface OtherOperationsList extends DomElement {
    @NotNull
    @SubTagList(Log.TAG_NAME)
    List<Log> getLogList();
}
