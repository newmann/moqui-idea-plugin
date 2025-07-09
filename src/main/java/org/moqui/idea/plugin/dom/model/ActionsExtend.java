package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ActionsExtend extends CommonActions {
    String TAG_NAME = "actions-extend";
    String ATTR_TYPE = "type";
    String ATTR_WHEN = "when";

    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();

    @NotNull
    @Attribute(ATTR_WHEN)
    GenericAttributeValue<String> getWhen();


}
