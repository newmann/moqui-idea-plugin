package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface StandaloneFields extends DomElement {
    @NotNull
    @SubTag(Editable.TAG_NAME)
    Editable getEditable();

    @NotNull
    @SubTag(Label.TAG_NAME)
    Label getLabel();

    @NotNull
    @SubTag(Image.TAG_NAME)
    Image getImage();
    @NotNull
    @SubTag(Link.TAG_NAME)
    Link getLink();
    @NotNull
    @SubTag(DynamicDialog.TAG_NAME)
    DynamicDialog getDynamicDialog();
    @NotNull
    @SubTag(DynamicContainer.TAG_NAME)
    DynamicContainer getDynamicContainer();
    @NotNull
    @SubTag(ButtonMenu.TAG_NAME)
    ButtonMenu getButtonMenu();



}
