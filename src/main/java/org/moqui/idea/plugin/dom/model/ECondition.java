package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameConverter;
import org.moqui.idea.plugin.dom.converter.ViewEntityAliasConverter;

public interface ECondition extends DomElement {
    public static final String TAG_NAME = "econdition";
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
    @Convert(EntityFieldNameConverter.class)
    GenericAttributeValue<String> getFieldName();

    @NotNull
    @Attribute(ATTR_ENTITY_ALIAS)
    @Convert(ViewEntityAliasConverter.class)
    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_OPERATOR)
    GenericAttributeValue<String> getOperator();

    @NotNull
    @Attribute(ATTR_TO_ENTITY_ALIAS)
    @Convert(ViewEntityAliasConverter.class)
    GenericAttributeValue<String> getToEntityAlias();

    @NotNull
    @Attribute(ATTR_TO_FIELD_NAME)
    @Convert(EntityFieldNameConverter.class)
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

    @NotNull GenericAttributeValue<String> getFrom();
    @NotNull GenericAttributeValue<Boolean> getIgnoreIfEmpty();
    @NotNull GenericAttributeValue<String> getIgnore();

}
