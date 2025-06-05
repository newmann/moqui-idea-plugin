package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.AbstractMemberEntity;
import org.moqui.idea.plugin.reference.MoquiBaseReference;
import org.moqui.idea.plugin.util.EntityUtils;

import javax.swing.*;
import java.util.Collection;

/**
 * ViewEntity定义中的alias处理
 * alias在MemberEntity的属性EntityAlias中定义
 * alias只在当前的ViewEntity中定义
 */

public class ViewEntityAliasConverter extends ResolvingConverter<AbstractMemberEntity> implements CustomReferenceConverter<AbstractMemberEntity> {
    @Override
    public @Nullable AbstractMemberEntity fromString(@Nullable @NonNls String s, ConvertContext context) {
        if(s == null) return null;
        return EntityUtils.getViewEntityAbstractMemberEntityByAlias(context,s)
                .orElse(null);

    }

    @Override
    public @NotNull Collection<? extends AbstractMemberEntity> getVariants(ConvertContext context) {
        return EntityUtils.getViewEntityAbstractMemberEntity(context);
    }

    @Override
    public @Nullable String toString(@Nullable AbstractMemberEntity entity, ConvertContext context) {
        if(entity == null) return null;
        return entity.getEntityAlias().getStringValue();
    }

    @Override
    public @Nullable LookupElement createLookupElement(AbstractMemberEntity entity) {
        if(entity == null) {
            return super.createLookupElement(entity);
        }else{
            String s = entity.getEntityAlias().getStringValue();
            Icon icon = AllIcons.Ide.Gift;//todo 配置一个更合适的icon
            return LookupElementBuilder.create(entity,s)
                    .withIcon(icon)
                    .withCaseSensitivity(false);
        }
    }

    @Override
    public @Nullable PsiElement getPsiElement(@Nullable AbstractMemberEntity resolvedValue) {
        if(resolvedValue == null) return null;
        if(resolvedValue.getEntityAlias().getXmlAttributeValue()==null) return null;

        return resolvedValue.getEntityAlias().getXmlAttributeValue().getOriginalElement();
    }

    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<AbstractMemberEntity> value, PsiElement element, ConvertContext context) {
//        String aliasName = value.getStringValue();
//        Optional<AbstractMemberEntity> optEntity = EntityUtils.getViewEntityAbstractMemberEntityByAlias(context,aliasName);
//        if (optEntity.isEmpty()) return PsiReference.EMPTY_ARRAY;
        AbstractMemberEntity entityCommonAttribute = value.getValue();
        if(entityCommonAttribute == null) return PsiReference.EMPTY_ARRAY;

        PsiReference[] psiReferences = new PsiReference[1];

//        AbstractMemberEntity entityCommonAttribute = optEntity.get();

//        psiReferences[0] = new PsiRef(element,
//                new TextRange(1,
//                        aliasName.length()+1),
//                entityCommonAttribute.getEntityAlias().getXmlAttributeValue());
        psiReferences[0] = new MoquiBaseReference(element,
                new TextRange(1,
                        element.getTextLength()-1), //前后两个双英号
                entityCommonAttribute.getEntityAlias().getXmlAttributeValue());

        return psiReferences;

    }

    @Override
    public @InspectionMessage String getErrorMessage(@Nullable String s, ConvertContext context) {
        return new HtmlBuilder()
                .append("根据")
                .append("[" + s+"]")
                .append("找不到对应的Alias定义。")
                .toString();
    }
}
