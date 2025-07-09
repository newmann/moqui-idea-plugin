package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Iterate extends AllOperationsList {

    String TAG_NAME = "iterate";
    String ATTR_LIST = "list";

    String ATTR_ENTRY = "entry";
    @NotNull
    @Attribute(ATTR_LIST)
    GenericAttributeValue<String> getList();
    @NotNull
    @Attribute(ATTR_ENTRY)
    GenericAttributeValue<String> getEntry();


}
