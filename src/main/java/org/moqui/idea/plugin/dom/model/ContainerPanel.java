package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface ContainerPanel extends DomElement {

    public static final String TAG_NAME = "container-panel";

    @NotNull
    @SubTag(PanelHeader.TAG_NAME)
    PanelHeader getPanelHeader();
    @NotNull
    @SubTag(PanelLeft.TAG_NAME)
    PanelLeft getPanelLeft();
    @NotNull
    @SubTag(PanelCenter.TAG_NAME)
    PanelCenter getPanelCenter();
    @NotNull
    @SubTag(PanelRight.TAG_NAME)
    PanelRight getPanelRight();
    @NotNull
    @SubTag(PanelFooter.TAG_NAME)
    PanelFooter getPanelFooter();


    @NotNull GenericAttributeValue<String> getId();

}
