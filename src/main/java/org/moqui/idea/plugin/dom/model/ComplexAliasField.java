package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityFieldNameReferenceConverter;
import org.moqui.idea.plugin.dom.converter.ViewEntityAliasConverter;

public interface ComplexAliasField extends DomElement {
    
    String TAG_NAME = "complex-alias-field";
    
    String ATTR_ENTITY_ALIAS = "entity-alias";
    
    String ATTR_FIELD = "field";

    @NotNull
    @Attribute(ATTR_ENTITY_ALIAS)
    @Convert(ViewEntityAliasConverter.class)
    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_FIELD)
    @Referencing(EntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getField();

    @NotNull GenericAttributeValue<String> getDefaultValue();
    @NotNull GenericAttributeValue<String> getFunction();


}
