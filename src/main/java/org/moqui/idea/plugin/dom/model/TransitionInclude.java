package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface TransitionInclude extends AbstractTransition,AbstractLocation {
    public static final String TAG_NAME = "transition-include";
    @NotNull GenericAttributeValue<String> getMethod();
}
