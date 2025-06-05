package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FieldLayout extends DomElement {

    public static final String TAG_NAME = "field-layout";

    @NotNull
    @SubTagList(FieldGroup.TAG_NAME)
    List<FieldLayout> getFieldGroupList();
    @NotNull
    @SubTagList(FieldAccordion.TAG_NAME)
    List<FieldAccordion> getFieldAccordionList();

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

    @NotNull
    @SubTagList(FieldColRow.TAG_NAME)
    List<FieldColRow> getFieldColRowList();

    @NotNull GenericAttributeValue<String> getId();

}
