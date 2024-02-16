package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ErrorResponse extends AttListResponse {
    public static final String TAG_NAME = "error-response";

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

}
