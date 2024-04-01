package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.ConditinalResponsePresentationProvider;

import java.util.List;
@Presentation(provider = ConditinalResponsePresentationProvider.class)
public interface ConditionalResponse extends AttListResponse {
    public static final String TAG_NAME = "conditional-response";

    @NotNull
    @SubTag(Condition.TAG_NAME)
    Condition getCondition();

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

}
