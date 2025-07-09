package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Check extends DomElement {
    String TAG_NAME = "check";

    @NotNull
    @SubTagList(EntityOptions.TAG_NAME)
    List<EntityOptions> getEntityOptionsList();

    @NotNull
    @SubTagList(ListOptions.TAG_NAME)
    List<ListOptions> getListOptionsList();

    @NotNull
    @SubTagList(Option.TAG_NAME)
    List<Option> getOptionList();


    @NotNull
    GenericAttributeValue<String> getNoCurrentSelectedKey();
    @NotNull
    GenericAttributeValue<String> getAllChecked();
    @NotNull
    GenericAttributeValue<String> getContainerStyle();

}
