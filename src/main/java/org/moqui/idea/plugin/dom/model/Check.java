package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

import java.util.List;

public interface Check extends DomElement {
    public static final String TAG_NAME = "check";

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
