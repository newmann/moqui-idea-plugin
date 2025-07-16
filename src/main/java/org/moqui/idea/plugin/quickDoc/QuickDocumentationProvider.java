package org.moqui.idea.plugin.quickDoc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexService;
import org.moqui.idea.plugin.service.IndexViewEntity;
import org.moqui.idea.plugin.util.*;

import java.util.List;
import java.util.Optional;

import static org.moqui.idea.plugin.quickDoc.format.CommonDocumentationFormatter.*;

public class QuickDocumentationProvider extends AbstractDocumentationProvider {
    @Override
    public @Nullable
    @Nls String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return super.getQuickNavigateInfo(element, originalElement);
    }

    @Override
    public @Nullable
    @Nls String generateHoverDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

//    @Override
//    public @Nullable PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
//        if(link.equals("#jumpToEntity")) {
//
//        }
//        return super.getDocumentationElementForLink(psiManager, link, context);
//    }

    @Override
    public @Nullable
    @Nls String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        PsiFile psiFile ;
//如果是EntityFacadeXml和SeedData下的内容， 需要单独处理
        if(originalElement != null) {
            XmlTag curTag = MyDomUtils.getParentTag(originalElement).orElse(null);
            if ((curTag != null) && EntityFacadeXmlUtils.isEntityFacadeDefineTag(curTag)) {
                return generateEntityFacadeDoc(element,curTag);
            }
        }

        if(element == null){
            return null;
        }else {
            psiFile = element.getContainingFile();
        }

        if(psiFile == null) return null;

        if(MyDomUtils.isNotMoquiXmlFile(psiFile)) return null;

        if(MyDomUtils.isNotAttributeValue(element)) return null;


        final String tagName = MyDomUtils.getCurrentTagName(element).orElse(MyStringUtils.EMPTY_STRING);
        return switch (tagName) {
            case Entity.TAG_NAME -> generateEntityDoc(element);
            case ViewEntity.TAG_NAME -> generateViewEntityDoc(element);
            case Service.TAG_NAME -> generateServiceDoc(element);
            case Section.TAG_NAME -> generateSectionDoc(element);
            case ServiceInclude.TAG_NAME -> generateServiceIncludeDoc(element);
//            case Field.TAG_NAME -> new FieldDocumentTarget(element).toString();
//            case Relationship.TAG_NAME -> new RelationshipDocumentTarget(element).toString();

            default -> null;
        };
    }
    public static String generateEntityFacadeDoc(PsiElement element, @NotNull XmlTag originalTag){
        EntityFacadeXmlTagDescriptor descriptor = EntityFacadeXmlTagDescriptor.of(originalTag);
        if(descriptor.getTagType() == null) return "Nothing to show.";
        switch (descriptor.getTagType()){
            case Entity -> {
                if(descriptor.entityIsValid()) {
                    Entity entity = EntityUtils.getEntityByName(element.getProject(),descriptor.getEntityName()).orElse(null);
                    if(entity != null ) return generateEntityDoc(entity);
                }
            }
            case Service -> {
                if(descriptor.serviceIsValid()) {
                    final String tagName = MyDomUtils.getCurrentTagName(element).orElse(MyStringUtils.EMPTY_STRING);
                    switch (tagName) {
                        case Service.TAG_NAME -> {
                            return generateServiceDoc(element);
                        }
                        case ServiceInclude.TAG_NAME -> {
                            return generateServiceIncludeDoc(element);
                        }
                    }
                    ;
                }
//                if(descriptor.serviceIsValid()) {
//                    Service service = ServiceUtils.getServiceByFullName(element.getProject(), descriptor.getServiceCallName()).orElse(null);
//                    if(service != null) {
//                        return generateServiceDoc(service);
//                    }
//                }
            }
        }
        return "Not found target Doc.";
    }
    public static String generateEntityDoc(PsiElement element) {
        Entity entity;
//        if(element instanceof XmlTag xmlTag) {
//            entity  = EntityFacadeXmlUtils.getFacadeEntityFromXmlTag(xmlTag)
//                    .flatMap(descriptor->EntityUtils.getEntityByName(element.getProject(),descriptor.getEntityName()))
//                    .orElse(null);
//        }else {
            entity = MyDomUtils.getLocalDomElementByPsiElement(element, Entity.class).orElse(null);
//        }
        return generateEntityDoc(entity);
    }
    private static String generateEntityDoc(@Nullable  Entity entity){
        if (entity == null){return "Not found target Entity.";}

        List<ExtendEntity> extendEntityCollection = EntityUtils.getExtendEntityListByName(entity.getManager().getProject(),
                MyDomUtils.getValueOrEmptyString(entity.getEntityName()));

        HtmlBuilder docBuilder = new HtmlBuilder()
                .append(formatEntityDefinition(entity));
        if (! entity.getFieldList().isEmpty()) {
            docBuilder.append(formatFieldList(entity.getFieldList()));
        }
        if(! extendEntityCollection.isEmpty()) {
            docBuilder.append(formatExtendEntityList(extendEntityCollection));
        }
        return docBuilder.toString();

    }
    public static String generateViewEntityDoc(PsiElement element) {
        ViewEntity view = MyDomUtils.getLocalDomElementByPsiElement(element,ViewEntity.class).orElse(null);
        if (view == null){return "Not found target View Entity.";}


        HtmlBuilder docBuilder = new HtmlBuilder()
                .append(formatViewEntityDefinition(view));
        //显示ViewEntity的字段定义
        Optional<IndexViewEntity> indexViewEntityOptional = EntityUtils.getIndexViewEntityByViewEntity(view);

        if(indexViewEntityOptional.isPresent()) {
                List<IndexAbstractField> abstractFieldList = indexViewEntityOptional.get().getIndexAbstractFieldList();
                if(abstractFieldList.isEmpty()) {
                    docBuilder.append("Not define view field");
                }else{
                    docBuilder.append(formatIndexAbstractFieldList(abstractFieldList));
                }
        }else {
            docBuilder.append("Not define view content");
        }

        return docBuilder.toString();
    }

    public static String generateServiceDoc(PsiElement element) {
        Service service = MyDomUtils.getLocalDomElementByPsiElement(element,Service.class).orElse(null);
        return generateServiceDoc(service);
//        if (service == null){return "Not found target Service.";}
//
//        HtmlBuilder docBuilder = new HtmlBuilder()
//                .append(formatServiceDefinition(service));
//
//        //通过IndexService来获取parameters
//        IndexService indexService = ServiceUtils.getIndexServiceOrInterface(element.getProject(),ServiceUtils.getFullNameFromService(service))
//                .orElse(null);
//        docBuilder.append(formatIndexServiceInOutParameter(indexService));
//        return docBuilder.toString();
    }
    public static String generateServiceDoc(@Nullable Service service){
        if (service == null){return "Not found target Service.";}

        HtmlBuilder docBuilder = new HtmlBuilder()
                .append(formatServiceDefinition(service));

        //通过IndexService来获取parameters
        IndexService indexService = ServiceUtils.getIndexServiceOrInterface(service.getManager().getProject(),ServiceUtils.getFullNameFromService(service))
                .orElse(null);
        docBuilder.append(formatIndexServiceInOutParameter(indexService));
        return docBuilder.toString();
    }
    public static String generateServiceIncludeDoc(PsiElement element) {
        ServiceInclude serviceInclude = MyDomUtils.getLocalDomElementByPsiElement(element,ServiceInclude.class).orElse(null);
        return generateServiceIncludeDoc(serviceInclude);
    }
    public static String generateServiceIncludeDoc(@Nullable ServiceInclude serviceInclude){
        if (serviceInclude == null){return "Not found target Service Include.";}

        HtmlBuilder docBuilder = new HtmlBuilder()
                .append(formatServiceIncludeDefinition(serviceInclude));

        //通过IndexService来获取parameters
        IndexService indexService = ServiceUtils.getIndexService(serviceInclude.getManager().getProject(),ServiceUtils.getFullNameFromServiceInclude(serviceInclude))
                .orElse(null);
        docBuilder.append(formatIndexServiceInOutParameter(indexService));
        return docBuilder.toString();

    }
    public static String generateSectionDoc(PsiElement element) {
        Section section = MyDomUtils.getLocalDomElementByPsiElement(element,Section.class).orElse(null);
        if (section == null){return "Not found target Section.";}

        HtmlBuilder docBuilder = new HtmlBuilder()
                .append(formatSectionDefinition(section));

        docBuilder.append(formatTagValue(section.getXmlTag(), null, "Not found target Section."));


        return docBuilder.toString();
    }
}

