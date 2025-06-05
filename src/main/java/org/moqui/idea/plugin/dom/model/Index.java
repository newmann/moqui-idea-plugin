package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Index extends DomElement {
    
    public static final String TAG_NAME = "index";

    public static final String ATTR_NAME ="name";

    
    public static final String ATTR_UNIQUE ="unique";

    
    public static final String ATTR_JOIN_FROM_ALIAS ="join-from-alias";

    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();

    @NotNull
    @Attribute(ATTR_UNIQUE)
    GenericAttributeValue<Boolean> getUnique();

    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();
    @NotNull
    @SubTagList(IndexField.TAG_NAME)
    List<IndexField> getIndexFieldList();

}
