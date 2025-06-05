package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EntityCondition extends DomElement {
    
    public static final String TAG_NAME = "entity-condition";
    
    public static final String ATTR_DISTINCT = "distinct";

    @NotNull
    @Attribute(ATTR_DISTINCT)
    GenericAttributeValue<Boolean> getDistinct();
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
    @SubTagList(OrderBy.TAG_NAME)
    List<OrderBy> getOrderByList();
    @NotNull
    @SubTag(HavingEConditions.TAG_NAME)
    HavingEConditions getHavingEConditions();
}
