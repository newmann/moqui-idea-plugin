package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ContainerRow extends DomElement {
    
    public static final String TAG_NAME = "container-row";

    @NotNull
    @SubTagList(RowCol.TAG_NAME)
    List<RowCol> getRowColList();

//    @NotNull GenericAttributeValue<String> getLocation();
    @NotNull GenericAttributeValue<String> getId();
    @NotNull GenericAttributeValue<String> getStyle();


}
