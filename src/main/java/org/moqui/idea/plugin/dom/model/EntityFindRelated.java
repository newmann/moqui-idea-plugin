package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface EntityFindRelated extends DomElement {
    
    String TAG_NAME = "entity-find-related";

    @NotNull GenericAttributeValue<String> getValueField();
    @NotNull GenericAttributeValue<String> getRelationshipName();
    @NotNull GenericAttributeValue<String> getMap();
    @NotNull GenericAttributeValue<String> getOrderByList();
    @NotNull GenericAttributeValue<Boolean> getCache();
    @NotNull GenericAttributeValue<Boolean> getForUpdate();
    @NotNull GenericAttributeValue<String> getToList();


}
