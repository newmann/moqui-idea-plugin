package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Iterate extends AllOperationsList {
    public static final String TAG_NAME = "iterate";
    public static final String ATTR_LIST = "list";
    public static final String ATTR_ENTRY = "entry";
    @NotNull
    @Attribute(ATTR_LIST)
    GenericAttributeValue<String> getList();
    @NotNull
    @Attribute(ATTR_ENTRY)
    GenericAttributeValue<String> getEntry();


}
