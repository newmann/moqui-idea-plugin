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
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import java.util.Collection;
import java.util.Optional;

/**
 * 处理ExtendEntity的entity-name属性
 */

public class ExtendEntityNameConverter extends AbstractExtendEntityAttributeConverter {

    @Override
    public @Nullable String getToString(@Nullable Entity entity, ConvertContext context) {
        return MyDomUtils.getXmlAttributeValueString(entity.getEntityName()).orElse(MyStringUtils.EMPTY_STRING);
    }

    @Override
    PsiElement getCreateReferenceElement(@NotNull Entity entity) {
        return entity.getEntityName().getXmlAttributeValue();
    }


}
