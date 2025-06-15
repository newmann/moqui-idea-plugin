package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Dialog extends StandaloneFieldsList {
    
    public static final String TAG_NAME = "dialog";

    
    public static final String ATTR_BUTTON_TEXT = "button-text";
    
    public static final String ATTR_BUTTON_TYPE = "button-type";
    
    public static final String ATTR_BUTTON_ICON = "button-icon";
    
    public static final String ATTR_TITLE = "title";
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

