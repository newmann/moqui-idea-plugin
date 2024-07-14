package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.*;
import icons.MoquiIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 对应到Entity的全称，含package，即为｛package｝.{entityName}
 */
public class EntityFullNameConverter extends ResolvingConverter<AbstractEntity> implements CustomReferenceConverter {
    @Override
    public @Nullable AbstractEntity fromString(@Nullable @NonNls String s, ConvertContext context) {
        if(s == null) return null;
        return EntityUtils.getEntityOrViewEntityByName(context.getProject(),s)
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
                result.addAll(EntityUtils.getAllEntityCollection(context.getProject()));
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
            result.addAll(EntityUtils.getAllEntityAndViewEntityCollection(context.getProject()));
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
//    public @Nullable PsiElement getPsiElement(@Nullable AbstractEntity resolvedValue) {
//        if (resolvedValue == null) return null;
//
//        return resolvedValue.getXmlTag().getOriginalElement();
//    }

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


    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {

        String entityName = value.getStringValue();
        return EntityUtils.createEntityNameReferences(context.getProject(),element, entityName,1);


    }
}
