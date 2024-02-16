package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AllWidgetsList extends DomElement {

    @NotNull
    @SubTagList(FormSingle.TAG_NAME)
    List<FormSingle> getFormSingleList();
    @NotNull
    @SubTagList(FormList.TAG_NAME)
    List<FormList> getFormListList();

    @NotNull
    @SubTagList(SubScreensMenu.TAG_NAME)
    List<SubScreensMenu> getSubScreensMenuList();

    @NotNull
    @SubTagList(SubScreensActive.TAG_NAME)
    List<SubScreensActive> getSubScreensActiveList();

    @NotNull
    @SubTagList(SubScreensPanel.TAG_NAME)
    List<SubScreensPanel> getSubScreensPanelList();

    @NotNull
    @SubTagList(Section.TAG_NAME)
    List<Section> getSectionList();

    @NotNull
    @SubTagList(SectionIterate.TAG_NAME)
    List<SectionIterate> getSectionIterateList();

    @NotNull
    @SubTagList(SectionInclude.TAG_NAME)
    List<SectionInclude> getSectionIncludeList();

    @NotNull
    @SubTagList(Container.TAG_NAME)
    List<Container> getContainerList();

    @NotNull
    @SubTagList(ContainerBox.TAG_NAME)
    List<ContainerBox> getContainerBoxList();

    @NotNull
    @SubTagList(ContainerRow.TAG_NAME)
    List<ContainerRow> getContainerRowList();

    @NotNull
    @SubTagList(ContainerPanel.TAG_NAME)
    List<ContainerPanel> getContainerPanelList();

    @NotNull
    @SubTagList(ContainerDialog.TAG_NAME)
    List<ContainerDialog> getContainerDialogList();

    @NotNull
    @SubTagList(IncludeScreen.TAG_NAME)
    List<IncludeScreen> getIncludeScreenList();

    @NotNull
    @SubTagList(Tree.TAG_NAME)
    List<Tree> getTreeList();

    @NotNull
    @SubTagList(RenderMode.TAG_NAME)
    List<RenderMode> getRenderModeList();

    @NotNull
    @SubTagList(Text.TAG_NAME)
    List<Text> getTextList();

}
