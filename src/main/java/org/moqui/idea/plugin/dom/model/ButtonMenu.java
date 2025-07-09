package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ButtonMenu extends DomElement {
    
    String TAG_NAME = "button-menu";

    @NotNull
    @SubTagList(Link.TAG_NAME)
    List<Link> getLinkList();
    @NotNull
    @SubTagList(Label.TAG_NAME)
    List<Label> getLabelList();
    @NotNull
    @SubTagList(DynamicDialog.TAG_NAME)
    List<DynamicDialog> getDynamicDialogList();
    @NotNull
    @SubTagList(ContainerDialog.TAG_NAME)
    List<ContainerDialog> getContainerDialogList();

    @NotNull GenericAttributeValue<String> getText();
    @NotNull GenericAttributeValue<String> getTextMap();
    @NotNull GenericAttributeValue<String> getEncode();
    @NotNull GenericAttributeValue<String> getIcon();
    @NotNull GenericAttributeValue<String> getTooltip();
    @NotNull GenericAttributeValue<String> getBtnType();
    @NotNull GenericAttributeValue<String> getCondition();
}
