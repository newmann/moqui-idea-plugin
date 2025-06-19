package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Index extends DomElement {
    
    String TAG_NAME = "index";

    String ATTR_NAME ="name";

    
    String ATTR_UNIQUE ="unique";

    
    String ATTR_JOIN_FROM_ALIAS ="join-from-alias";

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
