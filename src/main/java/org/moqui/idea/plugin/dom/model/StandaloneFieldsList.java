package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface StandaloneFieldsList extends DomElement {
    @NotNull
    @SubTagList(Editable.TAG_NAME)
    List<Editable> getEditableList();

    @NotNull
    @SubTagList(Label.TAG_NAME)
    List<Label> getLabelList();

    @NotNull
    @SubTagList(Image.TAG_NAME)
    List<Image> getImageList();
    @NotNull
    @SubTagList(Link.TAG_NAME)
    List<Link> getLinkList();
    @NotNull
    @SubTagList(DynamicDialog.TAG_NAME)
    List<DynamicDialog> getDynamicDialogList();
    @NotNull
    @SubTagList(DynamicContainer.TAG_NAME)
    List<DynamicContainer> getDynamicContainerList();
    @NotNull
    @SubTagList(ButtonMenu.TAG_NAME)
    List<ButtonMenu> getButtonMenuList();
}
