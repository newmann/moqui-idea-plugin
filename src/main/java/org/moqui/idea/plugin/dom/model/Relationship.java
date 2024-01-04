package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Relationship extends DomElement {
    public static final String TAG_NAME = "relationship";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_RELATED = "related";
    public static final String ATTR_TITLE = "title";
    public static final String ATTR_SHORT_ALIAS = "short_alias";
    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();

    @NotNull
    @Attribute(ATTR_RELATED)
    GenericAttributeValue<String> getRelated();


    @NotNull
    @Attribute(ATTR_TITLE)
    GenericAttributeValue<String> getTitle();

    @NotNull
    @Attribute(ATTR_SHORT_ALIAS)
    GenericAttributeValue<Boolean> getShortAlias();
}
