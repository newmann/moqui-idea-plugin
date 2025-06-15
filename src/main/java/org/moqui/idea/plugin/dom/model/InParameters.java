package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface InParameters extends DomElement {
    
    public static final String TAG_NAME = "in-parameters";

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();
    @NotNull
    @SubTagList(AutoParameters.TAG_NAME)
    List<AutoParameters> getAutoParametersList();
}
