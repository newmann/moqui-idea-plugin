package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface ControlOperations extends DomElement {
    @NotNull
    @SubTag(Break.TAG_NAME)
    Break getBreak();

    @NotNull
    @SubTag(Continue.TAG_NAME)
    Continue getContinue();
    @NotNull
    @SubTag(Iterate.TAG_NAME)
    Iterate getIterate();
    @NotNull
    @SubTag(Message.TAG_NAME)
    Message getMessage();
    @NotNull
    @SubTag(CheckErrors.TAG_NAME)
    CheckErrors getCheckErrors();

    @NotNull
    @SubTag(Return.TAG_NAME)
    Return getReturn();

}
