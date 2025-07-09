package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FieldRowBig extends DomElement {
    
    String TAG_NAME = "field-row-big";

    @NotNull
    @SubTagList(FieldRef.TAG_NAME)
    List<FieldRef> getFieldRefList();

    @NotNull GenericAttributeValue<String> getTitle();

}
