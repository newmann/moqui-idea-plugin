package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface ServiceCall extends DomElement {
    public static final String TAG_NAME = "service-call";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_IN_MAP = "in_map";
    public static final String ATTR_OUT_MAP = "out_map";
    public static final String ATTR_OUT_MAP_ADD_TO_EXISTING = "out_map_add_to_existing";
    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();

    @NotNull
    @Attribute(ATTR_IN_MAP)
    GenericAttributeValue<String> getInMap();

    @NotNull
    @Attribute(ATTR_OUT_MAP)
    GenericAttributeValue<String> getOutMap();
    @NotNull
    @Attribute(ATTR_OUT_MAP_ADD_TO_EXISTING)
    GenericAttributeValue<String> getOutMapAddToExisting();

}
