package org.moqui.idea.plugin.provider;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.util.EntityScope;
import org.moqui.idea.plugin.util.EntityUtils;

public class EntityFacadeXmlReferenceProvider extends PsiReferenceProvider {
    private static Logger logger = Logger.getInstance(EntityFacadeXmlReferenceProvider.class);
    public static EntityFacadeXmlReferenceProvider of(){
        return new EntityFacadeXmlReferenceProvider();
    }

    @Override
    public @NotNull  PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if(psiElement.getParent() instanceof XmlTag xmlTag) {
            if(!xmlTag.getName().equals(EntityFacadeXml.TAG_NAME)) {
                return EntityUtils.createEntityOrViewNameReferences(psiElement.getProject(), xmlTag, EntityScope.ENTITY_ONLY);
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }


}
