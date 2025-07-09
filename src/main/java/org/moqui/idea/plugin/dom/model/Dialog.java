package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Dialog extends StandaloneFieldsList {
    
    String TAG_NAME = "dialog";

    
    String ATTR_BUTTON_TEXT = "button-text";
    
    String ATTR_BUTTON_TYPE = "button-type";
    
    String ATTR_BUTTON_ICON = "button-icon";
    
    String ATTR_TITLE = "title";
    @NotNull
    @Attribute(ATTR_BUTTON_TEXT)
    GenericAttributeValue<String> getButtonText();

    @NotNull
    @Attribute(ATTR_BUTTON_TYPE)
    GenericAttributeValue<String> getButtonType();
    @NotNull
    @Attribute(ATTR_BUTTON_ICON)
    GenericAttributeValue<String> getButtonIcon();
    @NotNull
    @Attribute(ATTR_TITLE)
    GenericAttributeValue<String> getTitle();


}

