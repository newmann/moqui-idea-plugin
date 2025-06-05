package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface AutoWidgetService extends DomElement {
    
    public static final String TAG_NAME = "auto-widget-service";

    @NotNull GenericAttributeValue<String> getServiceName();
    @NotNull GenericAttributeValue<String> getParameterName();
    @NotNull GenericAttributeValue<String> getFieldType();

}
