package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Emecas extends DomElement {
    public static final String TAG_NAME = "emecas";

    @NotNull
    @SubTagList(Emeca.TAG_NAME)
    List<Emeca> getEmecaList();
}
