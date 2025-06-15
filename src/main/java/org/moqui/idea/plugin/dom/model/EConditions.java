package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EConditions extends DomElement {

    public static final String TAG_NAME = "econditions";

    public static final String ATTR_COMBINE = "combine";
    @NotNull
    @Attribute(ATTR_COMBINE)
    GenericAttributeValue<String> getCombine();
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
}
