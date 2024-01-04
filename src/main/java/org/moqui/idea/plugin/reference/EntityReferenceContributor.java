package org.moqui.idea.plugin.reference;

import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EntityReferenceContributor extends PsiReferenceContributor {
    private static final String ENTITY_NAME_PREFIX_STR = "entity-name";
    public static final PsiElementPattern.Capture<XmlAttribute> PATTERN_ATTR_ENTITY_NAME = PlatformPatterns.psiElement(XmlAttribute.class)
            .withText(StandardPatterns.string().startsWith(ENTITY_NAME_PREFIX_STR));
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PATTERN_ATTR_ENTITY_NAME,
                new PsiReferenceProvider() {
                    @Override
                    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                          @NotNull ProcessingContext context) {
                        if(!(element instanceof XmlAttribute)) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        XmlAttribute xmlAttribute = (XmlAttribute) element;
//                        PsiReferenceData userData = element.getUserData(PsiReferenceData.ENTITY_REFERENCES);
//                        if(userData != null && xmlAttribute.getContainingFile().getModificationStamp() == userData.getModificationStamp()) {
//                            return userData.getPsiReferences().toArray(new PsiReference[userData.getPsiReferences().size()]);
//                        }
                        Project project = element.getProject();
                        String entityFullName = xmlAttribute.getValue();
                        EntityUtils.EntityDescriptor entityDescriptor = EntityUtils.getEntityDescriptorFromFullName(entityFullName);
                        Optional<XmlElement[]> entityElements = EntityUtils.findEntityByEntityDescriptor(project,entityDescriptor);
                        if(entityElements.isEmpty()) return PsiReference.EMPTY_ARRAY;

                        PsiReference[] psiReferences = new PsiReference[1];


                        PsiRef psiRef = new PsiRef(xmlAttribute,new TextRange(0,entityFullName.length()),entityElements.get()[0]);
                        psiReferences[0] = psiRef;

//
//                        List<PsiReference> references = new ArrayList<PsiReference>();
//                        references.add(psiRef);
//
//                        xmlAttribute.putUserData(PsiReferenceData.ENTITY_REFERENCES,
//                                new PsiReferenceData(xmlAttribute.getContainingFile().getModificationStamp(),
//                                        references));


                        return psiReferences;

                    }
                });

    }

}
