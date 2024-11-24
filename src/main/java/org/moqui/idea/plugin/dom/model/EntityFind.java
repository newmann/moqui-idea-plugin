package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.EntityFindPresentationProvider;

import java.util.List;
@Presentation(provider = EntityFindPresentationProvider.class)
public interface EntityFind extends AbstractEntityName {
    public static final String TAG_NAME = "entity-find";
//    public static final String ATTR_ENTITY_NAME = "entity-name";
    public static final String ATTR_LIST = "list";
//    @NotNull
//    @Attribute(ATTR_ENTITY_NAME)
//    @Convert(EntityFullNameConverter.class)
//    GenericAttributeValue<String> getEntityName();

    @NotNull
    @Attribute(ATTR_LIST)
    GenericAttributeValue<String> getList();

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

    @NotNull
    @SubTagList(OrderBy.TAG_NAME)
    List<OrderBy> getOrderByList();

    @NotNull
    @SubTag(LimitRange.TAG_NAME)
    LimitRange getLimitRange();
    @NotNull
    @SubTag(LimitView.TAG_NAME)
    LimitView getLimitView();
    @NotNull
    @SubTag(UseIterator.TAG_NAME)
    UseIterator getUseIterator();

    @NotNull GenericAttributeValue<Boolean> getCache();
    @NotNull GenericAttributeValue<Boolean> getForUpdate();
    @NotNull GenericAttributeValue<Boolean> getDistinct();
    @NotNull GenericAttributeValue<Boolean> getUseClone();
    @NotNull GenericAttributeValue<String> getOffset();
    @NotNull GenericAttributeValue<String> getLimit();

}
