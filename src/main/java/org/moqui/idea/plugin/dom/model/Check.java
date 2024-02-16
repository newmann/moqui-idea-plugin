package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Check extends DomElement {
    public static final String TAG_NAME = "check";

    @NotNull
    @SubTagList(EntityOptions.TAG_NAME)
    List<Set> getSetList();

    @NotNull GenericAttributeValue<String> getLocation();

}
