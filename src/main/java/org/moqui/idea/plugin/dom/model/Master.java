package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Master extends DomElement {
    public static final String TAG_NAME = "master";

    @NotNull
    @SubTagList(Detail.TAG_NAME)
    List<Detail> getDetails();

    @NotNull
    GenericAttributeValue<String> getName();

}
