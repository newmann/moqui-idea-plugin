package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.EntityScope;
import org.moqui.idea.plugin.util.EntityUtils;

/**
 * 对应到Entity的名称
 */

public class EntityNameReferenceConverter implements CustomReferenceConverter<String> {

//    @Override
//    public @NotNull Collection<? extends String> getVariants(ConvertContext context) {
//        return new ArrayList<>();
//    }

    @Override
    public @NotNull PsiReference[] createReferences(GenericDomValue<String> genericDomValue, PsiElement psiElement, ConvertContext convertContext) {
        return EntityUtils.createEntityOrViewNameReferences(psiElement.getProject(),psiElement, EntityScope.ENTITY_ONLY);
    }
}
