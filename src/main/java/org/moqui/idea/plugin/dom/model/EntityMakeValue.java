package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EntityMakeValue extends DomElement {
    public static final String TAG_NAME = "entity-make-value";


    @NotNull GenericAttributeValue<String> getEntityName();
    @NotNull GenericAttributeValue<String> getMap();
    @NotNull GenericAttributeValue<String> getValueField();



}
