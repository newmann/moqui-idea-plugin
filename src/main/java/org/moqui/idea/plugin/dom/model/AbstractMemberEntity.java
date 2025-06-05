package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.ViewEntityAliasConverter;

public interface AbstractMemberEntity extends DomElement {

    public static final String ATTR_ENTITY_ALIAS ="entity-alias";
    public static final String ATTR_JOIN_FROM_ALIAS ="join-from-alias";
    @NotNull
    @Attribute(ATTR_ENTITY_ALIAS)
    @NameValue
    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_JOIN_FROM_ALIAS)
    @Convert(ViewEntityAliasConverter.class)
    GenericAttributeValue<String> getJoinFromAlias();

}
