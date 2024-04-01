package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface AllOperations extends CallOperations,
        EnvOperations,EntityMiscOperations,EntityFindOperations,
        EntityValueOperations,
//        EntityListOperations,
        ControlOperations,IfBasicOperations,IfOtherOperations,OtherOperations
    {

}
