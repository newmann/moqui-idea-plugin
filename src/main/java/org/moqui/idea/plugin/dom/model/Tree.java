package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Tree extends DomElement {

    public static final String TAG_NAME = "tree";


    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();
    @NotNull
    @SubTagList(TreeNode.TAG_NAME)
    List<TreeNode> getTreeNodeList();
    @NotNull
    @SubTagList(TreeSubNode.TAG_NAME)
    List<TreeSubNode> getTreeSubNodeList();

    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getTransition();
    @NotNull GenericAttributeValue<String> getOpenPath();

}
