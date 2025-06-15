package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.DefaultResponsePresentationProvider;

import java.util.List;
@Presentation(provider = DefaultResponsePresentationProvider.class)
public interface DefaultResponse extends AttListResponse {
    
    public static final String TAG_NAME = "default-response";

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

}
