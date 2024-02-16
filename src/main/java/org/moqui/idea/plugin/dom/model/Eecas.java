package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Eecas extends DomElement {
    public static final String TAG_NAME = "eecas";

    @NotNull
    @SubTagList(Eeca.TAG_NAME)
    List<Eeca> getEecaList();
}
