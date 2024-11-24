package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface TreeSubNode extends DomElement {
    public static final String TAG_NAME = "tree-sub-node";


    @NotNull
    @SubTag(Actions.TAG_NAME)
    Actions getActions();

    @NotNull GenericAttributeValue<String> getNodeName();
    @NotNull GenericAttributeValue<String> getList();

}
