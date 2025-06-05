package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface AutoWidgetEntity extends DomElement {
    
    public static final String TAG_NAME = "auto-widget-entity";

    @NotNull GenericAttributeValue<String> getEntityName();
    @NotNull GenericAttributeValue<String> getFieldName();
    @NotNull GenericAttributeValue<String> getFieldType();

}
