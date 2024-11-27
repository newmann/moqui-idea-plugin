package org.moqui.idea.plugin.quickDoc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.ExtendEntity;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.dom.model.ViewEntity;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexViewEntity;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.ArrayList;
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

    @Override
    public @Nullable
    @Nls String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        PsiFile psiFile = element.getContainingFile();

        if(psiFile == null) return null;

        if(MyDomUtils.isNotMoquiXmlFile(psiFile)) return null;

        if(MyDomUtils.isNotAttributeValue(element)) return null;


        final String tagName = MyDomUtils.getCurrentTagName(element).orElse(MyStringUtils.EMPTY_STRING);
        return switch (tagName) {
            case Entity.TAG_NAME -> generateEntityDoc(element);
            case ViewEntity.TAG_NAME -> generateViewEntityDoc(element);
            case Service.TAG_NAME -> generateServiceDoc(element);
//            case Field.TAG_NAME -> new FieldDocumentTarget(element).toString();
//            case Relationship.TAG_NAME -> new RelationshipDocumentTarget(element).toString();

            default -> null;
        };
    }
    public static String generateEntityDoc(PsiElement element) {
        Entity entity = MyDomUtils.getLocalDomElementByPsiElement(element,Entity.class).orElse(null);
        if (entity == null){return "Not found target Entity.";}

        List<ExtendEntity> extendEntityCollection = EntityUtils.getExtendEntityListByName(element.getProject(),
                MyDomUtils.getValueOrEmptyString(entity.getEntityName())).orElse(new ArrayList<>());

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
        if (service == null){return "Not found target Service.";}

        HtmlBuilder docBuilder = new HtmlBuilder()
                .append(formatServiceDefinition(service));

        docBuilder.append(formatTagValue(service.getInParameters().getXmlTag(),"In Parameters:","Not define in parameters"));
        docBuilder.append(formatTagValue(service.getOutParameters().getXmlTag(),"Out Parameters:","Not define out parameters"));


        return docBuilder.toString();
    }
}
