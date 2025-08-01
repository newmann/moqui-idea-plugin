package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FieldColRow extends DomElement {

    String TAG_NAME = "field-col-row";

    @NotNull
    @SubTagList(FieldGroup.TAG_NAME)
    List<FieldGroup> getFieldGroupList();

    @NotNull GenericAttributeValue<String> getActive();

}
