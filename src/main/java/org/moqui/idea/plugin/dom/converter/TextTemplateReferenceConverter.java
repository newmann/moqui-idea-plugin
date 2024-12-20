package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.EntityFacadeXmlUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

/**
 *
 */
public class TextTemplateReferenceConverter implements CustomReferenceConverter<String> {


    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        return EntityFacadeXmlUtils.createTextTemplateReferences(element);

    }

}
