package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface WebappList extends DomElement {
    public static final String TAG_NAME = "webapp-list";

    @NotNull
    @SubTagList(Webapp.TAG_NAME)
    List<Webapp> getWebappList();

}
