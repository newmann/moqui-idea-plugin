package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ConditionalResponse extends AttListResponse {
    public static final String TAG_NAME = "conditional-response";

    @NotNull
    @SubTag(Condition.TAG_NAME)
    Condition getCondition();

    @NotNull
    @SubTag(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

}
