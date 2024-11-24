package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface Emeca extends DomElement {
    public static final String TAG_NAME = "emeca";

    @NotNull
    GenericAttributeValue<String> getRuleName();
    @NotNull
    @SubTag(Condition.TAG_NAME)
    Condition getCondition();
    @NotNull
    @SubTag(Actions.TAG_NAME)
    Actions getActions();
}
