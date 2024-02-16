package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Component extends DomElement {
    public static final String TAG_NAME = "component";

    @NotNull GenericAttributeValue<String> getLocation();
    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getVersion();

    @NotNull
    @SubTagList(DependsOn.TAG_NAME)
    List<DependsOn> getDependsOnList();

}
