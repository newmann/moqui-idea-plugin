package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FilterMapList extends DomElement {
    
    public static final String TAG_NAME = "filter-map-list";

    @NotNull
    @SubTagList(FieldMap.TAG_NAME)
    List<FieldMap> getFieldMapList();

    @NotNull
    @SubTagList(DateFilter.TAG_NAME)
    List<DateFilter> getDateFilterList();

    @NotNull
    GenericAttributeValue<String> getList();

    @NotNull
    GenericAttributeValue<String> getToList();
}
