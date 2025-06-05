package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface SubScreensMenu extends DomElement {

    public static final String TAG_NAME = "subscreens-menu";


//    @NotNull
//    @SubTagList(ConditionalDefault.TAG_NAME)
//    List<ConditionalDefault> getConditionalDefaultList();
    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getId();
    @NotNull GenericAttributeValue<String> getTitle();
    @NotNull GenericAttributeValue<String> getWidth();
    @NotNull GenericAttributeValue<String> getHeaderMenusId();

}
