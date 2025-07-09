package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameReferenceConverter;
import org.moqui.idea.plugin.dom.converter.ViewEntityAliasConverter;

public interface ECondition extends DomElement {

    String TAG_NAME = "econdition";

    String ATTR_ENTITY_ALIAS = "entity-alias";

    String ATTR_FIELD_NAME = "field-name";

    String ATTR_OPERATOR = "operator";

    String ATTR_TO_ENTITY_ALIAS = "to-entity-alias";

    String ATTR_TO_FIELD_NAME = "to-field-name";
    String ATTR_VALUE = "value";
    String ATTR_FROM = "from";

    String ATTR_IGNORE_CASE = "ignore-case";

    String ATTR_OR_NULL = "or-null";
    @NotNull
    @Attribute(ATTR_FIELD_NAME)
    @Referencing(EntityFieldNameReferenceConverter.class)
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
    @Referencing(EntityFieldNameReferenceConverter.class)
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

    @NotNull
    @Attribute(ATTR_FROM)
    GenericAttributeValue<String> getFrom();
    @NotNull GenericAttributeValue<Boolean> getIgnoreIfEmpty();
    @NotNull GenericAttributeValue<String> getIgnore();

}
