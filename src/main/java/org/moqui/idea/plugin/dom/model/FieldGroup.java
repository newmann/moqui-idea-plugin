package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FieldGroup extends DomElement {

    String TAG_NAME = "field-group";


    @NotNull GenericAttributeValue<String> getTitle();
    @NotNull GenericAttributeValue<String> getStyle();
    @NotNull GenericAttributeValue<Boolean> getBox();

    @NotNull
    @SubTagList(FieldRef.TAG_NAME)
    List<FieldRef> getFieldRefList();
    @NotNull
    @SubTagList(FieldsNotReferenced.TAG_NAME)
    List<FieldsNotReferenced> getFieldsNotReferencedList();
    @NotNull
    @SubTagList(FieldRow.TAG_NAME)
    List<FieldRow> getFieldRowList();
    @NotNull
    @SubTagList(FieldRowBig.TAG_NAME)
    List<FieldRowBig> getFieldRowBigList();


}
