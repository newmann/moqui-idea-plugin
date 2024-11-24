package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IfBasicOperationsList extends DomElement {
    @NotNull
    @SubTagList(Compare.TAG_NAME)
    List<Compare> getCompareList();

    @NotNull
    @SubTagList(Expression.TAG_NAME)
    List<Expression> getExpressionList();

}
