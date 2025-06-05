package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TreeNode extends DomElement {
    
    public static final String TAG_NAME = "tree-node";

    @NotNull
    @SubTag(Condition.TAG_NAME)
    Condition getCondition();

    @NotNull
    @SubTag(Actions.TAG_NAME)
    Actions getActions();
    @NotNull
    @SubTag(Link.TAG_NAME)
    Link getLink();
    @NotNull
    @SubTag(Label.TAG_NAME)
    Label getLabel();
    @NotNull
    @SubTagList(TreeSubNode.TAG_NAME)
    List<TreeSubNode> getTreeSubNodeList();

    @NotNull GenericAttributeValue<String> getName();


}
