package org.moqui.idea.plugin.reference;

import com.intellij.patterns.*;
import com.intellij.psi.xml.*;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EntityReferenceContributor extends PsiReferenceContributor {

//    public static final PsiElementPattern.Capture<XmlAttribute> PATTERN_ATTR_ENTITY_NAME =
//            PlatformPatterns.psiElement(XmlAttribute.class);
    public static final XmlAttributeValuePattern XML_ATTRIBUTE_VALUE_PATTERN = XmlPatterns.xmlAttributeValue()
            .withLocalName("entity-name","entity","related");

    private static final Set<String> ALLOW_FILE_TYPES = new HashSet<String>();
    static {
        ALLOW_FILE_TYPES.add(Services.TAG_NAME);
        ALLOW_FILE_TYPES.add(Screen.TAG_NAME);
        ALLOW_FILE_TYPES.add(Secas.TAG_NAME);
        ALLOW_FILE_TYPES.add(Eecas.TAG_NAME);
        ALLOW_FILE_TYPES.add(Entities.TAG_NAME);
    }


    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(XML_ATTRIBUTE_VALUE_PATTERN,
                new PsiReferenceProvider() {
                    @Override
                    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                          @NotNull ProcessingContext context) {

                        XmlAttributeValue attributeValue = (XmlAttributeValue) element;
                        PsiReferenceData cacheData = attributeValue.getUserData(PsiReferenceData.MOQUI_ENTITY_REFERENCES);
                        if((cacheData != null) && (attributeValue.getContainingFile().getModificationStamp() == cacheData.getModificationStamp())){
                            return cacheData.getPsiReferences();
                        }

                        XmlFile file = (XmlFile) element.getContainingFile();
                        final String rootTagName = file.getRootTag().getName();

                        if(!ALLOW_FILE_TYPES.contains(rootTagName)){
                            return PsiReference.EMPTY_ARRAY;
                        }
                        //需要根据所在文件类型和tag进行进行分别处理
                        //Services,Secas,Screen中没有entity和related，只有entity-name
                        //Eecas中没有entity-name和related，只有entity
                        //Entities中，只有在member-entity下的entity-name和related才需要处理，而在entity和view-entity下的entity-name不需要处理
                        if(rootTagName.equals(Entities.TAG_NAME)) {
                            XmlAttribute attribute = (XmlAttribute) attributeValue.getParent();
                            XmlTag tag = (XmlTag) attribute.getParent();
                            if(tag == null) return PsiReference.EMPTY_ARRAY;
                            final String tagName = tag.getName();
                            if(tagName.equals(Entity.TAG_NAME) || tagName.equals(ViewEntity.TAG_NAME)) return PsiReference.EMPTY_ARRAY;
                        }

                        Project project = element.getProject();
                        final String entityFullName = attributeValue.getValue();

//                        Optional<XmlElement> optEntity = EntityUtils.findEntityXmlElementByFullName(project,entityFullName);
                        Optional<Entity> optEntity = EntityUtils.findEntityByFullName(project,entityFullName);
                        if (optEntity.isEmpty()) return PsiReference.EMPTY_ARRAY;

//                        PsiReference[] psiReferences = new PsiReference[1];
//
//                        //获取目标
//                        XmlElement entity = optEntity.get();
//                        XmlTag entityTag = (XmlTag) entity.getOriginalElement();
//
//                        PsiRef psiRef = new PsiRef(attributeValue,new TextRange(1,entityFullName.length()+1), entityTag);
//
//                        psiReferences[0] = psiRef;

                        PsiReference[] psiReferences = new PsiReference[2];
                        Entity entity = optEntity.get();
                        AbstractEntity entityCommonAttribute = (AbstractEntity) entity;

//                        XmlTag entityTag = (XmlTag) entity.getOriginalElement();

                        //package reference
                        psiReferences[0] = new PsiRef(attributeValue,
                                new TextRange(1,entityCommonAttribute.getPackage().getStringValue().length()+1),
                                entityCommonAttribute.getPackage().getXmlAttributeValue());

                        //entityname reference
                        psiReferences[1] = new PsiRef(attributeValue,
                                new TextRange(entityCommonAttribute.getPackage().getStringValue().length()+2,
                                        entityFullName.length()+1),
                                entityCommonAttribute.getEntityName().getXmlAttributeValue());



                        //保存缓存
                        attributeValue.putUserData(PsiReferenceData.MOQUI_ENTITY_REFERENCES,
                                new PsiReferenceData(attributeValue.getContainingFile().getModificationStamp(), psiReferences));
                        
                        return psiReferences;

                    }
                });

    }

}
