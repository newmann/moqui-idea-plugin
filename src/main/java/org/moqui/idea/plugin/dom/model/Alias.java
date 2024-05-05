package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameConverter;
import org.moqui.idea.plugin.dom.converter.ViewEntityAliasConverter;
import org.moqui.idea.plugin.dom.presentation.AliasPresentationProvider;

@Presentation(provider = AliasPresentationProvider.class)
public interface Alias extends AbstractField {
    public static final String TAG_NAME = "alias";

    public static final String ATTR_ENTITY_ALIAS ="entity-alias";
    public static final String ATTR_FIELD="field";
//    public static final String ATTR_NAME = "name";
    public static final String ATTR_FUNCTION = "function";
    public static final String ATTR_IS_AGGREGATE = "is-aggregate";
//    public static final String ATTR_TYPE = "type";
    public static final String ATTR_DEFAULT_DISPLAY = "default-display";
    public static final String ATTR_PG_EXPRESSION = "pg-expression";
    @NotNull
    @Attribute(ATTR_ENTITY_ALIAS)
    @Convert(ViewEntityAliasConverter.class)
    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_FIELD)
    @Convert(EntityFieldNameConverter.class)
    GenericAttributeValue<String> getField();
//    @NotNull
//    @Attribute(ATTR_NAME)
//    GenericAttributeValue<String> getName();
    @NotNull
    @Attribute(ATTR_FUNCTION)
    GenericAttributeValue<String> getFunction();
    @NotNull
    @Attribute(ATTR_IS_AGGREGATE)
    GenericAttributeValue<Boolean> getIsAggregate();
//    @NotNull
//    @Attribute(ATTR_TYPE)
//    GenericAttributeValue<String> getType();
    @NotNull
    @Attribute(ATTR_DEFAULT_DISPLAY)
    GenericAttributeValue<String> getDefaultDisplay();
    @NotNull
    @Attribute(ATTR_PG_EXPRESSION)
    GenericAttributeValue<String> getPgExpression();

    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();
    @NotNull
    @SubTag(ComplexAlias.TAG_NAME)
    ComplexAlias getComplexAlias();

}
