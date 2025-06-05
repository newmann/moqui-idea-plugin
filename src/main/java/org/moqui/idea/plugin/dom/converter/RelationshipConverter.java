package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.MoquiBaseReference;
import org.moqui.idea.plugin.service.AbstractIndexEntity;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.service.IndexViewEntity;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * （Entity/ExtendEntity）-》master-》detail
 * relationship
 *
 */

public class RelationshipConverter extends ResolvingConverter<Relationship>  implements CustomReferenceConverter<Relationship> {
    @Override
    public @Nullable Relationship fromString(@Nullable @NonNls String s, ConvertContext context) {
        if(s == null) return null;
        return getRelationship(s,context)
                .orElse(null);

    }

    @Override
    public @NotNull Collection<? extends Relationship> getVariants(ConvertContext context) {
        return getRelationshipList(context);
    }

    @Override
    public @Nullable String toString(@Nullable Relationship relationship, ConvertContext context) {
        if (relationship == null) return null;
        return EntityUtils.getRelatedNameFromRelationship(relationship).orElse(null);
    }

    @Override
    public @Nullable LookupElement createLookupElement(Relationship relationship) {
        if(relationship == null) {
            return super.createLookupElement(relationship);
        }else {
            String s = EntityUtils.getRelatedNameFromRelationship(relationship).orElse(MyStringUtils.EMPTY_STRING);
            return LookupElementBuilder.create(s)
//                    .withTailText(MyDomUtils.getValueOrEmptyString(relationship.getTitle()),true)
                    .withCaseSensitivity(true)
                    .withTypeText(MyDomUtils.getValueOrEmptyString(relationship.getRelated()));

        }
    }

//    @Override
//    public @Nullable PsiElement getPsiElement(@Nullable Relationship resolvedValue) {
//        return super.getPsiElement(resolvedValue);
//    }

    @Override
    public void handleElementRename(GenericDomValue<Relationship> genericValue, ConvertContext context, String newElementName) {

        super.handleElementRename(genericValue, context, newElementName);
    }
//    @Override
//    public @Nullable PsiElement getPsiElement(@Nullable Relationship resolvedValue) {
//        if(resolvedValue == null) return null;
//
//        return resolvedValue.getRelated().getXmlAttributeValue().getOriginalElement();
//    }

    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        String related = value.getStringValue();
        if(related == null) return PsiReference.EMPTY_ARRAY;

        Optional<Relationship> opRelationship = getRelationship(related,context);

        if (opRelationship.isEmpty()) {
            //报错
            return MoquiBaseReference.createNullRefArray(element,new TextRange(1,related.length()+1));
        };

        final Relationship relationship = opRelationship.get();

        int charIndex = related.indexOf("#");
        if(charIndex< 0) {
//            PsiReference[] psiReferences = new PsiReference[1];
            if(MyDomUtils.getValueOrEmptyString(relationship.getShortAlias()).equals(related)) {

                return MoquiBaseReference.createOneRefArray(element,
                        new TextRange(1,
                                related.length()+1),
                        relationship.getShortAlias().getXmlAttributeValue());


            } else if (MyDomUtils.getValueOrEmptyString(relationship.getRelated()).equals(related)) {
//                psiReferences[0] = new MoquiBaseReference(element,
//                        new TextRange(1,
//                                related.length()+1),
//                        relationship.getRelated().getXmlAttributeValue());

                return MoquiBaseReference.createOneRefArray(element,
                        new TextRange(1,
                                related.length()+1),
                        relationship.getRelated().getXmlAttributeValue());
            }
        }else {
            //${title}#${related-entity-name}
            PsiReference[] psiReferences = new PsiReference[2];

            psiReferences[0] = new MoquiBaseReference(element,
                    new TextRange(1,
                            charIndex+1),
                    relationship.getTitle().getXmlAttributeValue());
            psiReferences[1] = new MoquiBaseReference(element,
                    new TextRange(charIndex+2,
                            related.length()+1),
                    relationship.getRelated().getXmlAttributeValue());
            return psiReferences;
        }
        return PsiReference.EMPTY_ARRAY;

    }
    /**
     * 根据当前位置找到所有可用的Relationship
     */
    private List<Relationship> getRelationshipList(ConvertContext context) {
        List<Relationship> result = new ArrayList<>();

        final String curAttributeName = MyDomUtils.getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String secondTagName = MyDomUtils.getSecondParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);



        //master-detail（relationship）

        if(secondTagName.equals(Master.TAG_NAME)) {
            //如果是ExtendEntity，找对应的Entity
            Optional<ExtendEntity> opExtendEntity = MyDomUtils.getLocalDomElementByConvertContext(context,ExtendEntity.class);
            if(opExtendEntity.isPresent()) {
                result.addAll(EntityUtils.getEntityRelationshipList(context.getProject(),
                        EntityUtils.getFullNameFromExtendEntity(opExtendEntity.get())));

            }else {
                result.addAll(EntityUtils.getCurrentEntityRelationshipList(context));
            }

        }else {

            if(secondTagName.equals(Detail.TAG_NAME)) {
                //在Detail对应的Relationship指向的entity中寻找
                XmlTag parentTag = MyDomUtils.getSecondParentTag(context).orElse(null);
                //利用先后关系，直接找已经创建的PsiReference
                if(parentTag != null) {
                    XmlAttribute searchedAttr = parentTag.getAttribute(Detail.ATTR_RELATIONSHIP);
                    if(searchedAttr != null) {
                        Optional<Relationship> opRelationship = getRelationshipFromDetailAttribute(searchedAttr);
                        opRelationship.ifPresent(relationship -> result.addAll(EntityUtils.getEntityRelationshipList(context.getProject(),
                                MyDomUtils.getValueOrEmptyString(relationship.getRelated()))));
                    }
                }
            }
        }

        //ViewEntity
        ViewEntity curViewEntity = EntityUtils.getCurrentViewEntity(context).orElse(null);
        if(curViewEntity != null) {
            //ViewEntity-MemberRelationship（relationship）
            MemberRelationship curMemberRelationship = EntityUtils.getCurrentMemberRelationship(context).orElse(null);
            if(curMemberRelationship != null) {
                //1、根據ViewEntity找到IndexViewEntity，
                // 2、在IndexViewEntity中，根据join-from-alias别名找到对应的IndexEntity
                // 3、从IndexEntity中找到可用的Relationship
                IndexViewEntity indexViewEntity = EntityUtils.getIndexViewEntityByViewEntity(curViewEntity).orElse(null);
                if(indexViewEntity != null) {
                    //MemberRelationship对应的一定是IndexEntity
                    AbstractIndexEntity abstractIndexEntity = indexViewEntity.getAbstractIndexEntityByAlias(
                            MyDomUtils.getValueOrEmptyString(curMemberRelationship.getJoinFromAlias())).orElse(null);
                    if((abstractIndexEntity instanceof IndexEntity indexEntity)) {
                        result.addAll(indexEntity.getEntity().getRelationshipList());
                    }

                }

            }

        }

        return result;

    }
    /**
     * 根据当前位置对应的Relationship
     */
    private Optional<Relationship> getRelationship(String related, ConvertContext context) {
        List<Relationship> relationshipList = getRelationshipList(context);
        return relationshipList.stream().filter(
                item->EntityUtils.isThisRelationshipRelatedName(item,related)
        ).findFirst();

    }

    /**
     * 从Detail的relationship属性的PsiReference中找到对应的Relationship
     * @param relationshipAttribute Detail的relationship属性
     * @return Optional<Relationship>
     */
    private Optional<Relationship> getRelationshipFromDetailAttribute(XmlAttribute relationshipAttribute){
        XmlAttributeValue valueAttribute = relationshipAttribute.getValueElement();
        if(valueAttribute == null) return Optional.empty();

        PsiReference psiReference = valueAttribute.getReference();

        if (psiReference == null) return Optional.empty();

        PsiElement targetElement = psiReference.resolve();
        if (targetElement == null) return Optional.empty();

        return Optional.ofNullable(DomUtil.findDomElement(targetElement, Relationship.class));

    }

    @Override
    public @InspectionMessage String getErrorMessage(@Nullable String s, ConvertContext context) {
        if(s==null) {
            return super.getErrorMessage(s, context);
        }else {
            return "找不到[" + s + "]对应的Relationship定义。";
        }
    }
}
