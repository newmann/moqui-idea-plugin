package org.moqui.idea.plugin.contributor;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.provider.EntityFacadeXmlReferenceProvider;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class EntityFacadeXmlReferenceContributor extends PsiReferenceContributor {
    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> ENTITY_FACADE_TAG_PATTERN =
            psiElement().withElementType(XmlTokenType.XML_NAME).inside(XmlPatterns.xmlTag().withLocalName(EntityFacadeXml.TAG_NAME))
        ;



    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(ENTITY_FACADE_TAG_PATTERN, EntityFacadeXmlReferenceProvider.of(),PsiReferenceRegistrar.HIGHER_PRIORITY);

    }
}
