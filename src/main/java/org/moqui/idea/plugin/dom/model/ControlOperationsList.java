package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ControlOperationsList extends DomElement {
    @NotNull
    @SubTagList(Break.TAG_NAME)
    List<Break> getBreakList();

    @NotNull
    @SubTagList(Continue.TAG_NAME)
    List<Continue> getContinueList();
    @NotNull
    @SubTagList(Iterate.TAG_NAME)
    List<Iterate> getIterateList();
    @NotNull
    @SubTagList(Message.TAG_NAME)
    List<Message> getMessageList();
    @NotNull
    @SubTagList(CheckErrors.TAG_NAME)
    List<CheckErrors> getCheckErrorsList();

    @NotNull
    @SubTagList(Return.TAG_NAME)
    List<Return> getReturnList();

}
