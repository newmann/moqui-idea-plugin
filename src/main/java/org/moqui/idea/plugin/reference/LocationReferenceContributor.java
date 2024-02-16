package org.moqui.idea.plugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.ScreenUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LocationReferenceContributor extends PsiReferenceContributor {

//    public static final PsiElementPattern.Capture<XmlAttribute> PATTERN_ATTR_ENTITY_NAME =
//            PlatformPatterns.psiElement(XmlAttribute.class);
    public static final XmlAttributeValuePattern XML_ATTRIBUTE_VALUE_PATTERN = XmlPatterns.xmlAttributeValue()
            .withLocalName("location")
        .inside(XmlPatterns.xmlTag().withLocalName(SubScreensItem.TAG_NAME,
                Screen.TAG_NAME,WidgetTemplateInclude.TAG_NAME,TransitionInclude.TAG_NAME));

    private static final Set<String> ALLOW_FILE_TYPES = new HashSet<String>();
    static {
//        ALLOW_FILE_TYPES.add(Services.TAG_NAME);
        ALLOW_FILE_TYPES.add(Screen.TAG_NAME);
//        ALLOW_FILE_TYPES.add(Secas.TAG_NAME);
//        ALLOW_FILE_TYPES.add(Eecas.TAG_NAME);
    }


    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(XML_ATTRIBUTE_VALUE_PATTERN,
                new PsiReferenceProvider() {
                    @Override
                    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                          @NotNull ProcessingContext context) {

                        XmlAttributeValue attributeValue = (XmlAttributeValue) element;
                        final String attributeText = element.getText();

                        PsiReferenceData cacheData = attributeValue.getUserData(PsiReferenceData.MOQUI_LOCATION_REFERENCES);
                        if((cacheData != null) && (attributeValue.getContainingFile().getModificationStamp() == cacheData.getModificationStamp())){
                            return cacheData.getPsiReferences();
                        }

                        XmlFile file = (XmlFile) element.getContainingFile();
                        final String rootTagName = file.getRootTag().getName();

                        if(!ALLOW_FILE_TYPES.contains(rootTagName)){
                            return PsiReference.EMPTY_ARRAY;
                        }

                        final Project project = element.getProject();
                        final String location = attributeValue.getValue();

                        Optional<DomFileElement<Screen>> optScreenFile = ScreenUtils.findScreenFileByLocation(project,location);
                        if (optScreenFile.isEmpty()) return PsiReference.EMPTY_ARRAY;

                        TextRange textRange = new TextRange(1,location.length()+1);

                        PsiReference[] psiReferences = new PsiReference[1];

                        //获取目标
                        XmlElement targetElement = optScreenFile.get().getRootElement().getXmlElement();

                        PsiRef psiRef = new PsiRef(attributeValue,textRange, targetElement);

                        psiReferences[0] = psiRef;

                        //保存缓存
                        attributeValue.putUserData(PsiReferenceData.MOQUI_LOCATION_REFERENCES,
                                new PsiReferenceData(attributeValue.getContainingFile().getModificationStamp(), psiReferences));
                        
                        return psiReferences;

                    }
                });

    }

}
