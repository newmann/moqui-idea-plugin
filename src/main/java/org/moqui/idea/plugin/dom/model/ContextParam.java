package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface ContextParam extends Actions {
    
    public static final String TAG_NAME = "context-param";

    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getValue();

}
