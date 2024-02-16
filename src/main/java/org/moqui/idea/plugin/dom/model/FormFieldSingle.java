package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FormFieldSingle extends FormFieldBase {

    @NotNull
    @SubTagList(ConditionalField.TAG_NAME)
    List<ConditionalField> getConditionalFieldList();

    @NotNull
    @SubTag(DefaultField.TAG_NAME)
    DefaultField getDefaultField();

}
