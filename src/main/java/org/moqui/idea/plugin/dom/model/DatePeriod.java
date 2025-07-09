package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface DatePeriod extends DomElement {
    
    String TAG_NAME = "date-period";


    @NotNull GenericAttributeValue<Boolean> getAllowEmpty();
    @NotNull GenericAttributeValue<Boolean> getTime();
}
