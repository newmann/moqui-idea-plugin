package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EntityFindRelatedOne extends DomElement {
    public static final String TAG_NAME = "entity-find-related-one";

    @NotNull GenericAttributeValue<String> getValueField();
    @NotNull GenericAttributeValue<String> getRelationshipName();
    @NotNull GenericAttributeValue<String> getCache();
    @NotNull GenericAttributeValue<String> getForUpdate();
    @NotNull GenericAttributeValue<String> getToValueField();


}
