package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface IfBasicOperations extends DomElement {
    @NotNull
    @SubTag(Compare.TAG_NAME)
    Compare getCompare();

    @NotNull
    @SubTag(Expression.TAG_NAME)
    Expression getExpression();

}
