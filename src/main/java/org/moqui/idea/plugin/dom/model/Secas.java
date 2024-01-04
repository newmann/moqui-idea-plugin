package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Secas extends DomElement {
    public static final String TAG_NAME = "secas";
    @NotNull
    @SubTagList(Secas.TAG_NAME)
    List<Seca> getSecas();
}
