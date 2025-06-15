package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface RowSelection extends DomElement {
    
    public static final String TAG_NAME = "row-selection";

    @NotNull
    @SubTag(Action.TAG_NAME)
    Action getAction();
}
