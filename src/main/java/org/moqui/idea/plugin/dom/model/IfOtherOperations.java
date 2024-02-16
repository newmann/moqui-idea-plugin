package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface IfOtherOperations extends DomElement {
    @NotNull
    @SubTag(Assert.TAG_NAME)
    Assert getAssert();

    @NotNull
    @SubTag(If.TAG_NAME)
    If getIf();
    @NotNull
    @SubTag(While.TAG_NAME)
    While getWhile();
}
