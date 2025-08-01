package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.RelationshipReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.MemberRelationPresentationProvider;

@Presentation(provider = MemberRelationPresentationProvider.class)
public interface MemberRelationship extends AbstractMemberEntity {

    String TAG_NAME = "member-relationship";

//    String ATTR_ENTITY_ALIAS ="entity-alias";

    String ATTR_RELATIONSHIP ="relationship";

//    String ATTR_JOIN_FROM_ALIAS ="join-from-alias";

    String ATTR_JOIN_OPTIONAL ="join-optional";

    String ATTR_SUB_SELECT ="sub-select";
//    @NotNull
//    @Attribute(ATTR_ENTITY_ALIAS)
//    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_RELATIONSHIP)
    @Referencing(RelationshipReferenceConverter.class)
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
