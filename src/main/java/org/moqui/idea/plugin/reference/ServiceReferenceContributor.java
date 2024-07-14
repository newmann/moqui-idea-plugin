package org.moqui.idea.plugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
@Deprecated
public class ServiceReferenceContributor extends PsiReferenceContributor {

//    public static final PsiElementPattern.Capture<XmlAttribute> PATTERN_ATTR_ENTITY_NAME =
//            PlatformPatterns.psiElement(XmlAttribute.class);
    public static final XmlAttributeValuePattern XML_ATTRIBUTE_VALUE_PATTERN = XmlPatterns.xmlAttributeValue()
            .withLocalName("name")
        .inside(XmlPatterns.xmlTag().withLocalName(ServiceCall.TAG_NAME));

    private static final Set<String> ALLOW_FILE_TYPES = new HashSet<String>();
    static {
        ALLOW_FILE_TYPES.add(Services.TAG_NAME);
        ALLOW_FILE_TYPES.add(Screen.TAG_NAME);
        ALLOW_FILE_TYPES.add(Secas.TAG_NAME);
        ALLOW_FILE_TYPES.add(Eecas.TAG_NAME);
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

                        PsiReferenceData cacheData = attributeValue.getUserData(PsiReferenceData.MOQUI_SERVICE_REFERENCES);
                        if((cacheData != null) && (attributeValue.getContainingFile().getModificationStamp() == cacheData.getModificationStamp())){
                            return cacheData.getPsiReferences();
                        }

                        XmlFile file = (XmlFile) element.getContainingFile();
                        final String rootTagName = file.getRootTag().getName();

                        if(!ALLOW_FILE_TYPES.contains(rootTagName)){
                            return PsiReference.EMPTY_ARRAY;
                        }

                        final Project project = element.getProject();
                        final String serviceFullName = attributeValue.getValue();

                        Optional<String> optEntityName = EntityUtils.getEntityNameFromServiceCallName(serviceFullName);
                        Optional<XmlElement> optTargetElement;
                        String indexStr;
                        TextRange textRange;
                        if(optEntityName.isPresent()) {
                            final String entityFullName = optEntityName.get();
                             optTargetElement = EntityUtils.getEntityOrViewEntityXmlElementByName(project, entityFullName);
                             indexStr = entityFullName;

                        }else {
                            //todo to double check
                            optTargetElement = Optional.empty();
//                            optTargetElement = ServiceUtils.findServiceByFullName(project,serviceFullName);
                            indexStr = serviceFullName;


                        }
                        int index = attributeText.indexOf(indexStr);

                        textRange = new TextRange(index,attributeText.length()-1);



                        if (optTargetElement.isEmpty()) return PsiReference.EMPTY_ARRAY;

                        PsiReference[] psiReferences = new PsiReference[1];

                        //获取目标
                        XmlElement targetElement = optTargetElement.get();
                        XmlTag targetTag = (XmlTag) targetElement.getOriginalElement();

                        PsiRef psiRef = new PsiRef(attributeValue,textRange, targetTag);

                        psiReferences[0] = psiRef;

                        //保存缓存
                        attributeValue.putUserData(PsiReferenceData.MOQUI_ENTITY_REFERENCES,
                                new PsiReferenceData(attributeValue.getContainingFile().getModificationStamp(), psiReferences));
                        
                        return psiReferences;

                    }
                });

    }

}
