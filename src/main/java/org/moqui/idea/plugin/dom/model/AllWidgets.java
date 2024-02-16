package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface AllWidgets extends DomElement {

    @NotNull
    @SubTag(FormSingle.TAG_NAME)
    FormSingle getFormSingle();
    @NotNull
    @SubTag(FormList.TAG_NAME)
    FormList getFormList();

    @NotNull
    @SubTag(SubScreensMenu.TAG_NAME)
    SubScreensMenu getSubScreensMenu();

    @NotNull
    @SubTag(SubScreensActive.TAG_NAME)
    SubScreensActive getSubScreensActive();

    @NotNull
    @SubTag(SubScreensPanel.TAG_NAME)
    SubScreensPanel getSubScreensPanel();

    @NotNull
    @SubTag(Section.TAG_NAME)
    Section getSection();

    @NotNull
    @SubTag(SectionIterate.TAG_NAME)
    SectionIterate getSectionIterate();

    @NotNull
    @SubTag(SectionInclude.TAG_NAME)
    SectionInclude getSectionInclude();

    @NotNull
    @SubTag(Container.TAG_NAME)
    Container getContainer();

    @NotNull
    @SubTag(ContainerBox.TAG_NAME)
    ContainerBox getContainerBox();

    @NotNull
    @SubTag(ContainerRow.TAG_NAME)
    ContainerRow getContainerRow();
    @NotNull
    @SubTag(ContainerPanel.TAG_NAME)
    ContainerPanel getContainerPanel();
    @NotNull
    @SubTag(ContainerDialog.TAG_NAME)
    ContainerDialog getContainerDialog();
    @NotNull
    @SubTag(IncludeScreen.TAG_NAME)
    IncludeScreen getIncludeScreen();
    @NotNull
    @SubTag(Tree.TAG_NAME)
    Tree getTree();
    @NotNull
    @SubTag(RenderMode.TAG_NAME)
    RenderMode getRenderMode();
    @NotNull
    @SubTag(Text.TAG_NAME)
    Text getText();



}
