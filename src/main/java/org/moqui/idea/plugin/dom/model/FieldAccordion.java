package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FieldAccordion extends DomElement {
    
    public static final String TAG_NAME = "field-accordion";

    @NotNull
    @SubTagList(FieldGroup.TAG_NAME)
    List<FieldGroup> getFieldGroupList();

    @NotNull GenericAttributeValue<String> getActive();

}
