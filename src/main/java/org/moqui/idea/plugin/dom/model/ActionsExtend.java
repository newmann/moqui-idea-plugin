package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ActionsExtend extends CommonActions {
    public static final String TAG_NAME = "actions-extend";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_WHEN = "when";

    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();

    @NotNull
    @Attribute(ATTR_WHEN)
    GenericAttributeValue<String> getWhen();


}
