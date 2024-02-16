package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface OtherOperations extends DomElement {
    @NotNull
    @SubTag(Log.TAG_NAME)
    Log getLog();
}
