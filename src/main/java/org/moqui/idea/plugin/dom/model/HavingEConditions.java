package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HavingEConditions extends EConditions {
    public static final String TAG_NAME = "having-econditions";

    @NotNull
    @SubTagList(ECondition.TAG_NAME)
    List<ECondition> getEConditionList();

    @NotNull
    @SubTagList(EConditions.TAG_NAME)
    List<EConditions> getEConditionsList();

    @NotNull
    @SubTagList(DateFilter.TAG_NAME)
    List<DateFilter> getDateFilterList();
    @NotNull
    @SubTagList(EConditionObject.TAG_NAME)
    List<EConditionObject> getEConditionObjectList();

    @NotNull GenericAttributeValue<String> getCombine();

}
