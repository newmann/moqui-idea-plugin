package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SubScreens extends DomElement {
    public static final String TAG_NAME = "subscreens";


    @NotNull
    @SubTagList(ConditionalDefault.TAG_NAME)
    List<ConditionalDefault> getConditionalDefaultList();
    @NotNull
    @SubTagList(SubScreensItem.TAG_NAME)
    List<SubScreensItem> getSubScreensItemList();

    @NotNull GenericAttributeValue<String> getDefaultItem();
    @NotNull GenericAttributeValue<String> getAlwaysUseFullPath();

}
