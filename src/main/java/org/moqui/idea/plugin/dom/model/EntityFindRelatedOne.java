package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface EntityFindRelatedOne extends DomElement {
    
    String TAG_NAME = "entity-find-related-one";

    @NotNull GenericAttributeValue<String> getValueField();
    @NotNull GenericAttributeValue<String> getRelationshipName();
    @NotNull GenericAttributeValue<String> getCache();
    @NotNull GenericAttributeValue<String> getForUpdate();
    @NotNull GenericAttributeValue<String> getToValueField();


}
