package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface ElseIf extends AllOperationsList {

    public static final String TAG_NAME = "else-if";
    public static final String ATTR_CONDITION = "condition";

    @NotNull
    @Attribute(ATTR_CONDITION)
    GenericAttributeValue<String> getConditionAttr();
//todo check

    @NotNull
    @SubTag(Then.TAG_NAME)
    Then getThen();

    @NotNull
    @SubTag(Condition.TAG_NAME)
    Condition getCondition();

}
