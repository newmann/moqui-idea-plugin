package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Radio extends DomElement {

    public static final String TAG_NAME = "radio";

    @NotNull
    @SubTagList(EntityOptions.TAG_NAME)
    List<EntityOptions> getEntityOptionList();
    @NotNull
    @SubTagList(ListOptions.TAG_NAME)
    List<ListOptions> getListOptionsList();

    @NotNull
    @SubTagList(Option.TAG_NAME)
    List<Option> getOptionList();

    @NotNull GenericAttributeValue<String> getNoCurrentSelectedKey();

}
