package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface If extends AllOperationsList {
    public static final String TAG_NAME = "if";
    public static final String ATTR_CONDITION = "condition";

    @NotNull
    @Attribute(ATTR_CONDITION)
    GenericAttributeValue<String> getConditionAttr();

    @NotNull
    @SubTag(Condition.TAG_NAME)
    Condition getCondition();

    @NotNull
    @SubTag(Then.TAG_NAME)
    Then getThen();

    @NotNull
    @SubTag(Else.TAG_NAME)
    Else getElse();

    @NotNull
    @SubTagList(ElseIf.TAG_NAME)
    List<ElseIf> getElseIfList();
}
