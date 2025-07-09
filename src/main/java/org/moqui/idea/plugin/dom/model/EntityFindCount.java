package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.EntityFindCountPresentationProvider;

import java.util.List;
@Presentation(provider = EntityFindCountPresentationProvider.class)
public interface EntityFindCount extends AbstractEntityName {
    
    String TAG_NAME = "entity-find-count";


    @NotNull
    @SubTag(SearchFormInputs.TAG_NAME)
    SearchFormInputs getSearchFormInputs();

    @NotNull
    @SubTagList(DateFilter.TAG_NAME)
    List<DateFilter> getDateFilterList();

    @NotNull
    @SubTagList(ECondition.TAG_NAME)
    List<ECondition> getEConditionList();

    @NotNull
    @SubTagList(EConditions.TAG_NAME)
    List<EConditions> getEConditionsList();

    @NotNull
    @SubTagList(EConditionObject.TAG_NAME)
    List<EConditionObject> getEConditionObjectList();
    @NotNull
    @SubTag(HavingEConditions.TAG_NAME)
    HavingEConditions getHavingEConditions();
    @NotNull
    @SubTagList(SelectField.TAG_NAME)
    List<SelectField> getSelectFieldList();


    @NotNull GenericAttributeValue<String> getCountField();
    @NotNull GenericAttributeValue<String> getCache();
    @NotNull GenericAttributeValue<String> getDistinct();



}
