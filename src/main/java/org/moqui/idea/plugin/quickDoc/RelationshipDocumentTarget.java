package org.moqui.idea.plugin.quickDoc;

import com.intellij.model.Pointer;
import com.intellij.platform.backend.documentation.DocumentationResult;
import com.intellij.platform.backend.documentation.DocumentationTarget;
import com.intellij.navigation.TargetPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.ExtendEntity;
import org.moqui.idea.plugin.dom.model.Relationship;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.ArrayList;
import java.util.Collection;

public class RelationshipDocumentTarget implements DocumentationTarget {
    final private Entity entity;
    final private Relationship relationship;

    final private PsiElement element;
    Collection<ExtendEntity> extendEntityCollection;
    RelationshipDocumentTarget(@NotNull PsiElement element){

        this.element = element;
        this.relationship = MyDomUtils.getLocalDomElementByPsiElement(element,Relationship.class).orElse(null);
        final String entityName = MyDomUtils.getValueOrEmptyString(this.relationship.getRelated());

        this.entity = EntityUtils.getEntityByName(element.getProject(),entityName).orElse(null);
        if(this.entity != null) {
            extendEntityCollection = EntityUtils.getExtendEntityListByName(element.getProject(),
                    MyDomUtils.getValueOrEmptyString(this.entity.getEntityName())).orElse(new ArrayList<>());
        }

    }
    @NotNull
    @Override
    public Pointer<? extends DocumentationTarget> createPointer() {
        return Pointer.hardPointer(this);
    }


    @Nullable
    @Override
    public DocumentationResult computeDocumentation() {

        StringBuilder stringBuilder = new StringBuilder();
        if(this.relationship == null) {
            stringBuilder.append("<h1>");
            stringBuilder.append("Relationship is not found.");
            stringBuilder.append("</h1>");
        }else {
            String containingFile = this.relationship.getXmlElement().getContainingFile().getName();

            stringBuilder.append("<p>");
            String relationshipContent = this.relationship.getXmlElement().getText();

            if (relationshipContent != null) {
                relationshipContent = MyStringUtils.formatXmlForHtml(relationshipContent);
            } else {
                relationshipContent = "No content";
            }
//            stringBuilder.append("<b>");
            stringBuilder.append(relationshipContent);
//            stringBuilder.append("</b>");
            stringBuilder.append("</p>");
            stringBuilder.append("<p>");
            stringBuilder.append("Defined in " + containingFile);
            stringBuilder.append("</p>");
            stringBuilder.append("<p>");
            stringBuilder.append("Related Entity " + MyDomUtils.getValueOrEmptyString(this.relationship.getRelated()));
            stringBuilder.append("</p>");
            if (extendEntityCollection.size() > 0) {
                stringBuilder.append("<p>");
                stringBuilder.append("<b>");
                stringBuilder.append("Having [" + extendEntityCollection.size() + "] Extend Entity.");
                stringBuilder.append("</b>");
                stringBuilder.append("</p>");
            };

            stringBuilder.append("<hr>");
            stringBuilder.append("<pre>");
            //.replaceAll("\n|\r\n|\r", "<br>")
            String content = entity.getXmlElement().getText();

            if (content != null) {
                content = MyStringUtils.formatXmlForHtml(content);
            } else {
                content = "No content";
            }
            stringBuilder.append(content);
            stringBuilder.append("</pre>");

            extendEntityCollection.stream().forEach(extendEntity -> {
                stringBuilder.append("<hr style=\"width:50%\">");
                stringBuilder.append("<pre>");
                //.replaceAll("\n|\r\n|\r", "<br>")
                String extendEntityContent = extendEntity.getXmlElement().getText();
                if (extendEntityContent != null) {
                    extendEntityContent = MyStringUtils.formatXmlForHtml(extendEntityContent);
                } else {
                    extendEntityContent = "No content";
                }

                stringBuilder.append(extendEntityContent);
                stringBuilder.append("</pre>");
            });
        }
        return DocumentationResult.documentation(stringBuilder.toString());

    }

//    @NotNull
//    @Override
//    public TargetPresentation presentation() {
//        return TargetPresentation.builder(EntityUtils.getFullNameFromEntity(entity)).presentation();
//    }

    @NotNull
    @Override
    public TargetPresentation computePresentation() {
        return TargetPresentation.builder(EntityUtils.getFullNameFromEntity(entity)).presentation();
    }

    @Nullable
    @Override
    public String computeDocumentationHint() {
        return DocumentationTarget.super.computeDocumentationHint();
    }
}
