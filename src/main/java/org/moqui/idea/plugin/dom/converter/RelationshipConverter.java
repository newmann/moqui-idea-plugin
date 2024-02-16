package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;
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
 */

public class RelationshipConverter extends ResolvingConverter<Relationship> implements CustomReferenceConverter {
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
                    .withCaseSensitivity(false);

        }
    }

    @Override
    public @Nullable PsiElement getPsiElement(@Nullable Relationship resolvedValue) {
        return super.getPsiElement(resolvedValue);
    }

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
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        String related = value.getStringValue();
        Optional<Relationship> opRelationship = getRelationship(related,context);

        if (opRelationship.isEmpty()) return PsiReference.EMPTY_ARRAY;

        final Relationship relationship = opRelationship.get();

        int charIndex = related.indexOf("#");
        if(charIndex< 0) {
            PsiReference[] psiReferences = new PsiReference[1];
            if(relationship.getShortAlias().getXmlAttributeValue().getValue().equals(related)) {

                psiReferences[0] = new PsiRef(element,
                        new TextRange(1,
                                related.length()+1),
                        relationship.getShortAlias().getXmlAttributeValue());

                return psiReferences;

            }
            if(relationship.getRelated().getXmlAttributeValue().getValue().equals(related)) {
                psiReferences[0] = new PsiRef(element,
                        new TextRange(1,
                                related.length()+1),
                        relationship.getRelated().getXmlAttributeValue());

                return psiReferences;
            }
        }else {
            //${title}#${related-entity-name}
            PsiReference[] psiReferences = new PsiReference[2];
            final String title = relationship.getTitle().getXmlAttributeValue().getValue();
            final String entityName = relationship.getRelated().getXmlAttributeValue().getValue();

            psiReferences[0] = new PsiRef(element,
                    new TextRange(1,
                            charIndex+1),
                    relationship.getTitle().getXmlAttributeValue());
            psiReferences[1] = new PsiRef(element,
                    new TextRange(charIndex+2,
                            related.length()+1),
                    relationship.getRelated().getXmlAttributeValue());
            return psiReferences;
        }

        return PsiReference.EMPTY_ARRAY;

    }
    /**
     * 根据当前位置找到所有可用的Relationship
     *
     * @param context
     * @return
     */
    private List<Relationship> getRelationshipList(ConvertContext context) {
        List<Relationship> result = new ArrayList<>();

        final String curAttributeName = MyDomUtils.getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String secondTagName = MyDomUtils.getSecondParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);

//        XmlTag firstTag = context.getTag();
//        if (firstTag == null) return result;
//        if (!(firstTag.getName().equals(Detail.TAG_NAME))) return result;
//
//        XmlTag secondTag = firstTag.getParentTag();
//        if (secondTag == null) return result;
        //master-detail（relationship）

        if(secondTagName.equals(Master.TAG_NAME)) {
            //todo 在当前的entity中寻找，如果是ExtendEntity怎么办？
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

                XmlAttribute searchedAttr = parentTag.getAttribute(Detail.ATTR_RELATIONSHIP);
                Optional<Relationship> opRelationship = getRelationshipFromDetailAttribute(searchedAttr);
                if (opRelationship.isPresent()) {
                    result.addAll(EntityUtils.getEntityRelationshipList(context.getProject(),
                            opRelationship.get().getRelated().getXmlAttributeValue().getValue()));
                }
            }
        }

        //ViewEntity-MemberRelationship（relationship）
        if(secondTagName.equals(ViewEntity.TAG_NAME) && firstTagName.equals(MemberRelationship.TAG_NAME)) {
            ViewEntity currentViewEntity = EntityUtils.getCurrentViewEntity(context).orElse(null);
            MemberRelationship memberRelationship = EntityUtils.getCurrentMemberRelationship(context).orElse(null);
            MemberEntity memberEntity = EntityUtils.getMemberEntityByAlias(currentViewEntity,
                    memberRelationship.getJoinFromAlias().getXmlAttributeValue().getValue());
            if(memberEntity != null) {
                result.addAll(EntityUtils.getEntityRelationshipList(context.getProject(),
                        memberEntity.getEntityName().getXmlAttributeValue().getValue()));
            }
        }
        return result;

    }
    /**
     * 根据当前位置对应的Relationship
     * @param related
     * @param context
     * @return
     */
    private Optional<Relationship> getRelationship(String related, ConvertContext context) {
        List<Relationship> relationshipList = getRelationshipList(context);
        return relationshipList.stream().filter(
                item->{return EntityUtils.isThisRelationshipRelatedName(item,related);}
        ).findFirst();

//        XmlElement curElement = context.getXmlElement();
//        if (!(curElement instanceof XmlAttribute xmlAttribute)) return Optional.empty();
//        if (!(xmlAttribute.getName().equals(Detail.ATTR_RELATIONSHIP))) return Optional.empty();
//        Optional<Relationship> opRelationship = Optional.empty();
//
//        XmlTag firstTag = context.getTag();
//        if (firstTag == null) return Optional.empty();
//        if (!(firstTag.getName().equals(Detail.TAG_NAME))) return Optional.empty();
//
//        XmlTag secondTag = firstTag.getParentTag();
//        if (secondTag == null) return Optional.empty();
//        if(secondTag.getName().equals(Master.TAG_NAME)) {
//            //todo 在当前的entity中寻找，如果是ExtendEntity怎么办？
//            opRelationship =getRelationshipByShortAlias(getCurrentEntityRelationshipList(context),related);
//
//        }else {
//            //在Detail对应的Relationship指向的entity中寻找
//            XmlAttribute searchedAttr =  secondTag.getAttribute(Detail.ATTR_RELATIONSHIP);
//            Optional<Relationship> parentRelationship = getRelationshipFromDetailAttribute(searchedAttr);
//            if(parentRelationship.isPresent()) {
//                opRelationship = getRelationshipByShortAlias(
//                        EntityUtils.getEntityRelationshipList(context.getProject(),
//                                parentRelationship.get().getRelated().getXmlAttributeValue().getValue())
//                        ,related);
//            }
//
//        }
//
//        return opRelationship;
    }

    /**
     * 从Detail的relationship属性的PsiReference中找到对应的Relationship
     * @param relationshipAttribute
     * @return
     */
    private Optional<Relationship> getRelationshipFromDetailAttribute(XmlAttribute relationshipAttribute){
        PsiReference psiReference = relationshipAttribute.getValueElement().getReference();
        if (psiReference == null) return Optional.empty();

        PsiElement targetElement = psiReference.resolve();
        if (targetElement == null) return Optional.empty();

        return Optional.ofNullable(DomUtil.findDomElement(targetElement, Relationship.class));

    }


//
//    private Optional<Relationship> getRelationshipByShortAlias(List<Relationship> relationships,String alias){
//        return relationships.stream()
//                .filter(item ->{return EntityUtils.isThisRelationshipRelatedName(item,alias);})
//                .findFirst();
//    }
}
