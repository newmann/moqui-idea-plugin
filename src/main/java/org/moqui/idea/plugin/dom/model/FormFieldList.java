package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FormFieldList extends FormFieldBase {
    @NotNull
    @SubTag(HeaderField.TAG_NAME)
    HeaderField getHeaderField();
    @NotNull
    @SubTag(FirstRowField.TAG_NAME)
    FirstRowField getFirstRowField();
    @NotNull
    @SubTag(SecondRowField.TAG_NAME)
    SecondRowField getSecondRowField();

    @NotNull
    @SubTagList(ConditionalField.TAG_NAME)
    List<ConditionalField> getConditionalFieldList();

    @NotNull
    @SubTag(DefaultField.TAG_NAME)
    DefaultField getDefaultField();
    @NotNull
    @SubTag(LastRowField.TAG_NAME)
    LastRowField getLastRowField();

    @NotNull GenericAttributeValue<String> getPrintWidth();
    @NotNull GenericAttributeValue<String> getPrintWidthType();
    @NotNull GenericAttributeValue<String> getAlign();
    @NotNull GenericAttributeValue<String> getAggregate();
    @NotNull GenericAttributeValue<String> getShowTotal();
}
