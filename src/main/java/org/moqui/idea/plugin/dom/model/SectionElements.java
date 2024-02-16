package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface SectionElements extends DomElement {
    @NotNull
    @SubTag(Condition.TAG_NAME)
    Condition getCondition();

    @NotNull
    @SubTag(Actions.TAG_NAME)
    Actions getActions();

    @NotNull
    @SubTag(Widgets.TAG_NAME)
    Widgets getWidgets();

    @NotNull
    @SubTag(FailWidgets.TAG_NAME)
    FailWidgets getFailWidgets();

}
