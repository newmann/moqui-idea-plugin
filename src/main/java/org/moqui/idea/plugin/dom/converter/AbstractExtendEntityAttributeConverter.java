package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.moqui.idea.plugin.MyIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.converter.insertHandler.ExtendEntityNameAndPackageInsertionHandler;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.ExtendEntity;
import org.moqui.idea.plugin.reference.MoquiBaseReference;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import java.util.Collection;
import java.util.Optional;

/**
 * 处理ExtendEntity的name和package，两者是有关联的，必须对应到同一个Entity上
 */

public abstract class AbstractExtendEntityAttributeConverter extends ResolvingConverter<Entity> implements CustomReferenceConverter<Entity> {
    @Override
    public @Nullable Entity fromString(@Nullable String s, ConvertContext context) {
        if(s == null) return null;
        return getEntity(context).orElse(null);
    }

    @Override
    public @NotNull Collection<? extends Entity> getVariants(ConvertContext context) {
        return EntityUtils.getAllEntityCollection(context.getProject());
    }

    @Override
    public @Nullable String toString(@Nullable Entity entity, ConvertContext context) {
        return getToString(entity, context);
    }
    abstract @Nullable String getToString(@Nullable Entity entity, ConvertContext context);


    @Override
    public @Nullable LookupElement createLookupElement(Entity entity) {
        if(entity != null) {
            String s = EntityUtils.getFullNameFromEntity(entity);
//            Icon icon = AllIcons.Ide.Gift;//todo 配置一个更合适的icon
//            if(entity instanceof Entity) {
            Icon icon = MyIcons.EntityTag;
//            }
            return LookupElementBuilder.create(entity,s)
                    .withInsertHandler(ExtendEntityNameAndPackageInsertionHandler.INSTANCE)
                    .withIcon(icon)
                    .withCaseSensitivity(false);
        }else {
            return super.createLookupElement(entity);
        }
    }

    @Override
    public @NotNull PsiReference[] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        return getEntity(context).map(entity ->{
            PsiReference[] psiReferences = new PsiReference[1];
            //entityName reference
            psiReferences[0] = new MoquiBaseReference(element,
                    MyDomUtils.createAttributeTextRange((XmlAttributeValue)element),
                    getCreateReferenceElement(entity));
            return psiReferences;

        }).orElse(PsiReference.EMPTY_ARRAY);
    }

    abstract PsiElement getCreateReferenceElement(@NotNull Entity entity);

    protected Optional<String> getEntityFullName(ConvertContext context){
        XmlTag tag = context.getTag();
        if (tag == null) return Optional.empty();
        String entityName = tag.getAttributeValue(ExtendEntity.ATTR_ENTITY_NAME);
        String packageName = tag.getAttributeValue(ExtendEntity.ATTR_PACKAGE);
        if(entityName == null || packageName == null) return Optional.empty();
        return Optional.of(EntityUtils.getFullName(entityName,packageName));

    }
    protected Optional<Entity> getEntity(ConvertContext context){
        Optional<String> opEntityFullName = getEntityFullName(context);
        return opEntityFullName.map(s -> EntityUtils.getEntityByName(context.getProject(), s)).orElse(null);

    }
    @Override
    public @InspectionMessage String getErrorMessage(@Nullable String s, ConvertContext context) {
        String fullName = getEntityFullName(context).orElse(MyStringUtils.EMPTY_STRING);
        return new HtmlBuilder()
                .append("根据")
                .append("[" + fullName + "]")
                .append("找不到对应的Entity定义。")
                .toString();

    }
}
