package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import icons.MoquiIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.converter.insert.ExtendEntityNameAndPackageInsertionHandler;
import org.moqui.idea.plugin.dom.model.AbstractEntity;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.ViewEntity;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.EntityUtils;

import javax.swing.*;
import java.util.Collection;
import java.util.Optional;

/**
 * 对应到Entity的名称
 */

public class ExtendEntityNameConverter extends ResolvingConverter<Entity> implements CustomReferenceConverter {
    @Override
    public @Nullable Entity fromString(@Nullable @NonNls String s, ConvertContext context) {
        if(s == null) return null;
        return EntityUtils.findEntityByFullName(context.getProject(),s)
                .orElse(null);

    }

    @Override
    public @NotNull Collection<? extends Entity> getVariants(ConvertContext context) {
        return EntityUtils.findAllEntity(context.getProject());
    }

    @Override
    public @Nullable String toString(@Nullable Entity entity, ConvertContext context) {
        return entity.getEntityName().getXmlAttributeValue().getValue();
    }

    @Override
    public @Nullable LookupElement createLookupElement(Entity entity) {
        if(entity != null) {
            String s = EntityUtils.getFullNameFromEntity(entity);
            Icon icon = AllIcons.Ide.Gift;//todo 配置一个更合适的icon
            if(entity instanceof Entity) {
                icon = MoquiIcons.EntityTag;
            }
            return LookupElementBuilder.create(entity,s)
                    .withInsertHandler(ExtendEntityNameAndPackageInsertionHandler.INSTANCE)
                    .withIcon(icon)
                    .withCaseSensitivity(false);
        }else {
            return super.createLookupElement(entity);
        }
    }

    @Override
    public @Nullable PsiElement getPsiElement(@Nullable Entity resolvedValue) {
        if(resolvedValue == null) return null;

        return resolvedValue.getEntityName().getXmlAttributeValue().getOriginalElement();
    }

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        String entityFullName = value.getStringValue();
        Optional<Entity> optEntity = EntityUtils.findEntityByFullName(context.getProject(),entityFullName);
        if (optEntity.isEmpty()) return PsiReference.EMPTY_ARRAY;

        PsiReference[] psiReferences = new PsiReference[1];

        AbstractEntity entityCommonAttribute = optEntity.get();
        //entityname reference
        psiReferences[0] = new PsiRef(element,
                new TextRange(1,
                        entityFullName.length()+1),
                entityCommonAttribute.getEntityName().getXmlAttributeValue());

        return psiReferences;

    }
}
