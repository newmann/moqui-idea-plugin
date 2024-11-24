package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameConverter;
import org.moqui.idea.plugin.dom.converter.ViewEntityAliasConverter;

public interface ComplexAliasField extends DomElement {
    public static final String TAG_NAME = "complex-alias-field";
    public static final String ATTR_ENTITY_ALIAS = "entity-alias";
    public static final String ATTR_FIELD = "field";

    @NotNull
    @Attribute(ATTR_ENTITY_ALIAS)
    @Convert(ViewEntityAliasConverter.class)
    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_FIELD)
    @Convert(EntityFieldNameConverter.class)
    GenericAttributeValue<String> getField();

    @NotNull GenericAttributeValue<String> getDefaultValue();
    @NotNull GenericAttributeValue<String> getFunction();


}
