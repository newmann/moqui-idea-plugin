package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ConditionResponse extends AttListResponse {

    String TAG_NAME = "condition-response";


    String ATTR_NAME = "name";

    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();

    @NotNull
    @SubTag(Condition.TAG_NAME)
    Condition getCondition();

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

}
