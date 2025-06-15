package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface SubScreensPanel extends DomElement {
    
    public static final String TAG_NAME = "subscreens-panel";



    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getId();
    @NotNull GenericAttributeValue<String> getTitle();
    @NotNull GenericAttributeValue<String> getHeaderMenusId();

}
