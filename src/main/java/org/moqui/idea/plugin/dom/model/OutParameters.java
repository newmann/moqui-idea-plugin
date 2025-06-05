package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface OutParameters extends CommonActions {

    public static final String TAG_NAME = "out-parameters";

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParametersList();
    @NotNull
    @SubTagList(AutoParameters.TAG_NAME)
    List<AutoParameters> getAutoParametersList();
}
