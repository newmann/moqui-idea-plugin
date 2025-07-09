package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Condition extends IfConditions {
    String TAG_NAME = "condition";
    
    String ATTR_ENTITY_ALIAS = "entity-alias";
    
    String ATTR_FIELD_NAME = "field-name";
    
    String ATTR_OPERATOR = "operator";
    
    String ATTR_TO_ENTITY_ALIAS = "to-entity-alias";
    
    String ATTR_TO_FIELD_NAME = "to-field-name";
    String ATTR_VALUE = "value";
    
    String ATTR_IGNORE_CASE = "ignore-case";
    
    String ATTR_OR_NULL = "or-null";
    @NotNull
    @Attribute(ATTR_FIELD_NAME)
    GenericAttributeValue<String> getFieldName();
    @NotNull
    @Attribute(ATTR_ENTITY_ALIAS)
    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_OPERATOR)
    GenericAttributeValue<String> getOperator();
    @NotNull
    @Attribute(ATTR_TO_ENTITY_ALIAS)
    GenericAttributeValue<String> getToEntityAlias();
    @NotNull
    @Attribute(ATTR_TO_FIELD_NAME)
    GenericAttributeValue<String> getToFieldName();

    @NotNull
    @Attribute(ATTR_VALUE)
    GenericAttributeValue<String> getValue();
    @NotNull
    @Attribute(ATTR_IGNORE_CASE)
    GenericAttributeValue<Boolean> getIgnoreCase();
    @NotNull
    @Attribute(ATTR_OR_NULL)
    GenericAttributeValue<Boolean> getOrNull();


}
