package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface SubScreensActive extends DomElement {
    
    public static final String TAG_NAME = "subscreens-active";



    @NotNull GenericAttributeValue<String> getId();

}
