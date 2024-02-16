package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CallOperationsList extends DomElement {
    @NotNull
    @SubTagList(ServiceCall.TAG_NAME)
    List<ServiceCall> getServiceCallList();

    @NotNull
    @SubTagList(Script.TAG_NAME)
    List<Script> getScriptList();


}
