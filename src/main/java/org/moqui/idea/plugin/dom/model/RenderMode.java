package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RenderMode extends DomElement {
    
    public static final String TAG_NAME = "render-mode";


    @NotNull
    @SubTagList(Text.TAG_NAME)
    List<Text> getTextList();


}
