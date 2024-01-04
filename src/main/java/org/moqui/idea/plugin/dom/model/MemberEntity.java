package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MemberEntity extends DomElement {
    public static final String TAG_NAME = "member-entity";

    public static final String ATTR_ENTITY_ALIAS ="entity-alias";

    public static final String ATTR_ENTITY_NAME ="entity-name";

    public static final String ATTR_JOIN_FROM_ALIAS ="join-from-alias";

    @NotNull
    @Attribute(ATTR_ENTITY_ALIAS)
    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_ENTITY_NAME)
    GenericAttributeValue<String> getEntityName();

    @NotNull
    @Attribute(ATTR_JOIN_FROM_ALIAS)
    GenericAttributeValue<String> getJoinFromAlias();

    @NotNull
    @SubTagList(KeyMap.TAG_NAME)
    List<KeyMap> getKeyMaps();


}
