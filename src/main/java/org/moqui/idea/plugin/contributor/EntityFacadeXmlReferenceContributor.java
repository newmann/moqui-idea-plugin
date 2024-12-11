package org.moqui.idea.plugin.contributor;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.patterns.XmlTagPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyPatterns;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.provider.EntityFacadeXmlReferenceProvider;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PsiJavaPatterns.psiMethod;
import static com.intellij.patterns.XmlPatterns.xmlFile;
import static com.intellij.patterns.XmlPatterns.xmlTag;
import static org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyPatterns.groovyLiteralExpression;
@Deprecated
public class EntityFacadeXmlReferenceContributor extends PsiReferenceContributor {
    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> ENTITY_FACADE_XML_TAG_PATTERN =
            PlatformPatterns.psiElement().afterLeaf("<")
                    .inside(
                            xmlTag().withName(EntityFacadeXml.TAG_NAME).inside(xmlFile().withRootTag(xmlTag().withName(EntityFacadeXml.TAG_NAME)))
                    );

    //            PlatformPatterns.psiElement()
//                    .afterLeaf("<")


    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(ENTITY_FACADE_XML_TAG_PATTERN, EntityFacadeXmlReferenceProvider.of());

    }
}
