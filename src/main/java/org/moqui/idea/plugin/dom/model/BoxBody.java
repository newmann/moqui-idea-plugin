package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface BoxBody extends WidgetElementsList {
    
    public static final String TAG_NAME = "box-body";
    @NotNull GenericAttributeValue<String> getHeight();

}
