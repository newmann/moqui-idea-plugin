package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

import java.util.List;

public interface SubScreens extends DomElement {
    String TAG_NAME = "subscreens";
    String ATTR_DEFAULT_ITEM = "default-item";

    @NotNull
    @SubTagList(ConditionalDefault.TAG_NAME)
    List<ConditionalDefault> getConditionalDefaultList();
    @NotNull
    @SubTagList(SubScreensItem.TAG_NAME)
    List<SubScreensItem> getSubScreensItemList();

    @NotNull
    @Attribute(ATTR_DEFAULT_ITEM)
    @Convert(LocationConverter.class)
    GenericAttributeValue<String> getDefaultItem();
    @NotNull GenericAttributeValue<String> getAlwaysUseFullPath();

}
