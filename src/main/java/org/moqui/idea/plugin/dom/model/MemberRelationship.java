package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.RelationshipConverter;
import org.moqui.idea.plugin.dom.presentation.MemberRelationPresentationProvider;

@Presentation(provider = MemberRelationPresentationProvider.class)
public interface MemberRelationship extends AbstractMemberEntity {

    public static final String TAG_NAME = "member-relationship";

//    public static final String ATTR_ENTITY_ALIAS ="entity-alias";

    public static final String ATTR_RELATIONSHIP ="relationship";

//    public static final String ATTR_JOIN_FROM_ALIAS ="join-from-alias";

    public static final String ATTR_JOIN_OPTIONAL ="join-optional";

    public static final String ATTR_SUB_SELECT ="sub-select";
//    @NotNull
//    @Attribute(ATTR_ENTITY_ALIAS)
//    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_RELATIONSHIP)
    @Convert(RelationshipConverter.class)
    GenericAttributeValue<String> getRelationship();
//
//    @NotNull
//    @Attribute(ATTR_JOIN_FROM_ALIAS)
//    GenericAttributeValue<String> getJoinFromAlias();
    @NotNull
    @Attribute(ATTR_JOIN_OPTIONAL)
    GenericAttributeValue<Boolean> getJoinOptional();
    @NotNull
    @Attribute(ATTR_SUB_SELECT)
    GenericAttributeValue<String> getSubSelect();

    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();

    @NotNull
    @SubTag(EntityCondition.TAG_NAME)
    EntityCondition getEntityCondition();

}
