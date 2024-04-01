package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.openapi.util.text.HtmlChunk;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xml.*;
import icons.MoquiIcons;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.MoquiDomBundle;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.CustomNotifier;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 对应到Entity的全称，含package，即为｛package｝.{entityName}
 */
public class EntityFullNameConverter extends ResolvingConverter<AbstractEntity> implements CustomReferenceConverter {
    @Override
    public @Nullable AbstractEntity fromString(@Nullable @NonNls String s, ConvertContext context) {
        if(s == null) return null;
        return EntityUtils.findEntityAndViewEntityByFullName(context.getProject(),s)
                .orElse(null);
    }

    @Override
    public @NotNull Collection<? extends AbstractEntity> getVariants(ConvertContext context) {
        final String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        final String rootTagName = MyDomUtils.getRootTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        Collection<AbstractEntity> result = new ArrayList<AbstractEntity>();
        //Entities下的Relationship
        //Eecas下的Entity
        final java.util.Set<String> entityAllowedSet = java.util.Set.of(Relationship.TAG_NAME,Entity.TAG_NAME);
        if(entityAllowedSet.contains(firstTagName)){
                result.addAll(EntityUtils.findAllEntity(context.getProject()));
        }

        //Entities下的RMemberEntity
        //Services下的EntityFindOne，EntityFind
        //Serivces下的AutoParameters
        //Services下的Link
        //Resouce下的Entity
        final java.util.Set<String> entityAndViewAllowedSet = java.util.Set.of(MemberEntity.TAG_NAME
                ,EntityFind.TAG_NAME,EntityFindOne.TAG_NAME
                ,AutoParameters.TAG_NAME
                ,Link.TAG_NAME
                ,Entity.TAG_NAME
        );


        if (entityAndViewAllowedSet.contains(firstTagName)) {
            result.addAll(EntityUtils.findAllEntityAndViewEntity(context.getProject()));
        }


        return result;
    }

    @Override
    public @Nullable String toString(@Nullable AbstractEntity entity, ConvertContext context) {
        if(entity == null) return MyStringUtils.EMPTY_STRING;
        return EntityUtils.getFullNameFromEntity(entity);
    }



    @Override
    public @Nullable LookupElement createLookupElement(AbstractEntity entity) {
        if(entity != null) {
            String s = EntityUtils.getFullNameFromEntity(entity);
            Icon icon = AllIcons.Ide.Gift;//todo 配置一个更合适的icon
            if(entity instanceof Entity) {
                icon = MoquiIcons.EntityTag;
            }
            if(entity instanceof ViewEntity) {
                icon = MoquiIcons.ViewEntityTag;
            }
            return LookupElementBuilder.create(entity,s).withIcon(icon).withCaseSensitivity(true);
        }else {
            return super.createLookupElement(entity);
        }
    }

//    @Override
//    public @Nullable PsiElement resolve(AbstractEntity o, ConvertContext context) {
//        if(o != null) {
//            return o.getXmlTag().getOriginalElement();
//        }else {
//
//            return super.resolve(o, context);
//        }
//    }

    @Override
    public @Nullable PsiElement getPsiElement(@Nullable AbstractEntity resolvedValue) {
        if (resolvedValue == null) return null;

        return resolvedValue.getXmlTag().getOriginalElement();
    }

    @Override
    public @InspectionMessage String getErrorMessage(@Nullable String s, ConvertContext context) {
        return new HtmlBuilder()
                .append("根据")
                .append("[" + s+"]")
                .append("找不到对应的Entity定义。")
                .toString();
//        if(s == null )return null;
//
//        return "根据"+s+"找不到对应的Entity定义。";
    }

//    @Override
//    public void handleElementRename(GenericDomValue<AbstractEntity> genericValue, ConvertContext context, String newElementName) {
//        CustomNotifier.info(context.getProject(),newElementName);
//        super.handleElementRename(genericValue, context, newElementName);
//    }


    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {

        String entityFullName = value.getStringValue();
        return EntityUtils.createEntityFullNameReferences(context.getProject(),element,entityFullName,1);

//        Optional<AbstractEntity> optEntity = EntityUtils.findEntityAndViewEntityByFullName(context.getProject(),entityFullName);
//        if (optEntity.isEmpty()) return PsiReference.EMPTY_ARRAY;
//
//        List<PsiReference> psiReferenceList = new ArrayList<>();
//
//        AbstractEntity abstractEntity = optEntity.get();
//
//
//        if (entityFullName.indexOf(".")< 0) {
//            //没有含包名
//            //entityname reference or shortAlias reference
//            XmlAttributeValue xmlAttributeValue;
//            xmlAttributeValue = abstractEntity.getEntityName().getXmlAttributeValue();
//
//            if(abstractEntity instanceof Entity entity) {
//                Optional<String> optShortAlias = EntityUtils.getEntityShortAlias(entity);
//                if (optShortAlias.isPresent()) {
//                    if (optShortAlias.get().equals(entityFullName)) {
//                        xmlAttributeValue = entity.getShortAlias().getXmlAttributeValue();
//                    }
//                }
//            }
//
//            psiReferenceList.add(new PsiRef(element,
//                    new TextRange(1,
//                            entityFullName.length() + 1),
//                            xmlAttributeValue));
//
//        }else {
//            //package reference
//
//            psiReferenceList.add(new PsiRef(element,
//                    new TextRange(1,abstractEntity.getPackage().getStringValue().length()+1),
//                    abstractEntity.getPackage().getXmlAttributeValue()));
//
//
//            //entityname reference
//            psiReferenceList.add(new PsiRef(element,
//                    new TextRange(abstractEntity.getPackage().getStringValue().length()+2,
//                            entityFullName.length()+1),
//                    abstractEntity.getEntityName().getXmlAttributeValue()));
//
//        }
//
//
//        return psiReferenceList.toArray(new PsiReference[0]);
    }
}
