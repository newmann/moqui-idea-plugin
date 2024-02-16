package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface CallOperations extends DomElement {
    @NotNull
    @SubTag(ServiceCall.TAG_NAME)
    ServiceCall getServiceCall();

    @NotNull
    @SubTag(Script.TAG_NAME)
    Script getScript();


}
