package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface IfCombineOperations extends DomElement {
    @NotNull
    @SubTag(Or.TAG_NAME)
    Or getOr();

    @NotNull
    @SubTag(And.TAG_NAME)
    And getAnd();
    @NotNull
    @SubTag(Not.TAG_NAME)
    Not getNot();

}
