package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EntityDeleteByCondition extends AbstractEntityName {
    public static final String TAG_NAME = "entity-delete-by-condition";


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


//    @NotNull
//    @Convert(EntityFullNameConverter.class)
//    GenericAttributeValue<String> getEntityName();


}
