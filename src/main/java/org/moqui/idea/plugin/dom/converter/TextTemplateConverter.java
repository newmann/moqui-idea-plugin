package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.EntityFacadeXmlUtils;

import java.util.Collection;

/**
 *
 */
public class TextTemplateConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter<String> {


    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        return EntityFacadeXmlUtils.createTextTemplateReferences(element);

    }

    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext convertContext) {
        return EntityFacadeXmlUtils.getAllTextTemplateMap(convertContext.getProject()).keySet();
    }
}
