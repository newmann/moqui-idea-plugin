package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface Condition extends IfConditions {
    public static final String TAG_NAME = "condition";
    
    public static final String ATTR_ENTITY_ALIAS = "entity-alias";
    
    public static final String ATTR_FIELD_NAME = "field-name";
    
    public static final String ATTR_OPERATOR = "operator";
    
    public static final String ATTR_TO_ENTITY_ALIAS = "to-entity-alias";
    
    public static final String ATTR_TO_FIELD_NAME = "to-field-name";
    public static final String ATTR_VALUE = "value";
    
    public static final String ATTR_IGNORE_CASE = "ignore-case";
    
    public static final String ATTR_OR_NULL = "or-null";
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
