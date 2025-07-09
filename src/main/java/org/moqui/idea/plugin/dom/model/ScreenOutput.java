package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ScreenOutput extends DomElement {

    String TAG_NAME = "screen-output";

    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getMimeType();
    @NotNull GenericAttributeValue<String> getAlwaysStandalone();
    @NotNull GenericAttributeValue<String> getSkipActions();
    @NotNull GenericAttributeValue<String> getWidgetRenderClass();



}
