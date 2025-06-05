package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DropDown extends DomElement {

    public static final String TAG_NAME = "drop-down";

    @NotNull
    @SubTagList(EntityOptions.TAG_NAME)
    List<EntityOptions> getEntityOptionList();
    @NotNull
    @SubTagList(ListOptions.TAG_NAME)
    List<ListOptions> getListOptionsList();

    @NotNull
    @SubTagList(Option.TAG_NAME)
    List<Option> getOptionList();

    @NotNull
    @SubTag(DynamicOptions.TAG_NAME)
    DynamicOptions getDynamicOption();

    @NotNull GenericAttributeValue<Boolean> getAllowEmpty();
    @NotNull GenericAttributeValue<Boolean> getAllowMultiple();
    @NotNull GenericAttributeValue<Boolean> getSize();
    @NotNull GenericAttributeValue<String> getCurrent();
    @NotNull GenericAttributeValue<String> getNoCurrentSelectedKey();
    @NotNull GenericAttributeValue<Boolean> getRequiredManualSelect();
    @NotNull GenericAttributeValue<Boolean> getSubmitOnSelect();
    @NotNull GenericAttributeValue<String> getCurrentDescription();
    @NotNull GenericAttributeValue<String> getShowNot();
    @NotNull GenericAttributeValue<String> getStyle();

}
