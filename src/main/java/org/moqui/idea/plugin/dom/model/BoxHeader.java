package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface BoxHeader extends WidgetElementsList {
    public static final String TAG_NAME = "box-header";
    @NotNull GenericAttributeValue<String> getTitle();

}
