package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.ViewEntityPresentationProvider;

import java.util.List;
@Presentation(icon = "MoquiIcons.ViewEntityTag",provider = ViewEntityPresentationProvider.class)
public interface ViewEntity extends AbstractEntity {
    public static final String TAG_NAME = "view-entity";
//    public static final String ATTR_ENTITY_NAME = "entity-name";
//    public static final String ATTR_PACKAGE = "package";
    public static final String ATTR_CACHE = "cache";
    public static final String ATTR_AUTO_CLEAR_CACHE = "auto-clear-cache";
    public static final String ATTR_AUTHORIZE_SKIP = "authorize-skip";
//    @NotNull
//    @Attribute(ATTR_ENTITY_NAME)
//    GenericAttributeValue<String> getEntityName();
//
//    @NotNull
//    @Attribute(ATTR_PACKAGE)
//    @Convert(PackageAttributeConverter.class)
//    GenericAttributeValue<String> getPackage();
    @NotNull
    @Attribute(ATTR_CACHE)
    GenericAttributeValue<String> getCache();
    @NotNull
    @Attribute(ATTR_AUTO_CLEAR_CACHE)
    GenericAttributeValue<Boolean> getAutoClearCache();
    @NotNull
    @Attribute(ATTR_AUTHORIZE_SKIP)
    GenericAttributeValue<String> getAuthorizeSkip();

    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();

    @NotNull
    @SubTagList(MemberEntity.TAG_NAME)
    List<MemberEntity> getMemberEntityList();
    @NotNull
    @SubTagList(MemberRelationship.TAG_NAME)
    List<MemberRelationship> getMemberRelationshipList();

    @NotNull
    @SubTagList(AliasAll.TAG_NAME)
    List<AliasAll> getAliasAllList();

    @NotNull
    @SubTagList(Alias.TAG_NAME)
    List<Alias> getAliasList();
    @NotNull
    @SubTagList(Relationship.TAG_NAME)
    List<Relationship> getRelationshipList();

    @NotNull
    @SubTag(EntityCondition.TAG_NAME)
    EntityCondition getEntityCondition();

}
