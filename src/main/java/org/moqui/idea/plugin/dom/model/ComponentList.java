package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ComponentList extends DomElement {
    public static final String TAG_NAME = "component-list";

    @NotNull
    @SubTagList(ComponentDir.TAG_NAME)
    List<ComponentDir> getComponentDirList();
    @NotNull
    @SubTagList(Component.TAG_NAME)
    List<Component> getComponentList();


}
