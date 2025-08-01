package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityNameReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.MemberEntityPresentationProvider;

import java.util.List;
@Presentation(icon = "org.moqui.idea.plugin.MyIcons.MemberEntityTag", provider = MemberEntityPresentationProvider.class)
public interface MemberEntity extends AbstractMemberEntity {
    
    String TAG_NAME = "member-entity";

//    String ATTR_ENTITY_ALIAS ="entity-alias";

    
    String ATTR_ENTITY_NAME ="entity-name";

//    String ATTR_JOIN_FROM_ALIAS ="join-from-alias";
    
    String ATTR_JOIN_OPTIONAL ="join-optional";
    
    String ATTR_SUB_SELECT ="sub-select";
//    @NotNull
//    @Attribute(ATTR_ENTITY_ALIAS)
//    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_ENTITY_NAME)
//    @Convert(EntityFullNameConverter.class)
    @Referencing(EntityNameReferenceConverter.class)
    GenericAttributeValue<String> getEntityName();

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
    @SubTagList(KeyMap.TAG_NAME)
    List<KeyMap> getKeyMaps();
    @NotNull
    @SubTag(EntityCondition.TAG_NAME)
    EntityCondition getEntityCondition();

}
