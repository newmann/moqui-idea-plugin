package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface OrderMapList extends DomElement {
    public static final String TAG_NAME = "order-map-list";

    @NotNull
    @SubTagList(OrderBy.TAG_NAME)
    List<OrderBy> getOrderByList();


    @NotNull
    GenericAttributeValue<String> getList();


}
