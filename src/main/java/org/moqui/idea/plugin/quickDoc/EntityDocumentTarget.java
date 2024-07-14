package org.moqui.idea.plugin.quickDoc;

import com.intellij.icons.AllIcons;
import com.intellij.model.Pointer;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.platform.backend.documentation.DocumentationResult;
import com.intellij.platform.backend.documentation.DocumentationTarget;
import com.intellij.platform.backend.presentation.TargetPresentation;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.ExtendEntity;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class EntityDocumentTarget implements DocumentationTarget {
    final private Entity entity;
    final private PsiElement element;
    Collection<ExtendEntity> extendEntityCollection;
    EntityDocumentTarget(@NotNull PsiElement element){

        this.element = element;
        this.entity = MyDomUtils.getLocalDomElementByPsiElement(element,Entity.class).orElse(null);
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
        if(this.entity == null) {
            stringBuilder.append("<h1>");
            stringBuilder.append("Entity is not found.");
            stringBuilder.append("</h1>");
        }else {
            String containingFile = this.entity.getXmlElement().getContainingFile().getName();

            stringBuilder.append("<p>");
            stringBuilder.append("<b>");
            stringBuilder.append(EntityUtils.getFullNameFromEntity(this.entity));
            stringBuilder.append("</b>");
            stringBuilder.append("</p>");
            stringBuilder.append("<p>");
            stringBuilder.append("Defined in " + containingFile);
            stringBuilder.append("</p>");
            if (extendEntityCollection.size() > 0) {
                stringBuilder.append("<p>");
                stringBuilder.append("<b>");
                stringBuilder.append("Having [" + extendEntityCollection.size() + "] Extend Entity.");
                stringBuilder.append("</b>");
                stringBuilder.append("</p>");
            }
            ;

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

    @NotNull
    @Override
    public TargetPresentation computePresentation() {
        return new EntityTargetPresentation(entity);
    }

    @Nullable
    @Override
    public String computeDocumentationHint() {
        return "Entity: for example";
//        return DocumentationTarget.super.computeDocumentationHint();
    }

    @Nullable
    @Override
    public Navigatable getNavigatable() {
        if(entity != null) {
            return (Navigatable) entity.getXmlElement();
        }else {
            return null;
        }

    }
    private static class EntityTargetPresentation implements TargetPresentation {
        private Entity entity;
        EntityTargetPresentation(@NotNull Entity entity){
            this.entity = entity;
        }

        @Nullable
        @Override
        public Color getBackgroundColor() {
            return JBColor.CYAN;
        }

        @Nullable
        @Override
        public String getContainerText() {
            return "containerText";
        }

        @Nullable
        @Override
        public TextAttributes getContainerTextAttributes() {
            TextAttributes attributes = new TextAttributes();
            attributes.setBackgroundColor(JBColor.BLUE);
            return attributes;
        }

        @Nullable
        @Override
        public Icon getIcon() {
            return MoquiIcons.EntityTag;
        }

        @Nullable
        @Override
        public Icon getLocationIcon() {
            return AllIcons.Ide.ConfigFile;
        }

        @Nullable
        @Override
        public String getLocationText() {
            return "locationText";
        }

        @NotNull
        @Override
        public String getPresentableText() {
            return "presentableText";
        }

        @Nullable
        @Override
        public TextAttributes getPresentableTextAttributes() {
            TextAttributes attributes = new TextAttributes();
            attributes.setBackgroundColor(JBColor.RED);
            return attributes;
        }
    }
}
