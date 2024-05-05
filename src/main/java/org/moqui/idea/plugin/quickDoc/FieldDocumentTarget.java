package org.moqui.idea.plugin.quickDoc;

import com.intellij.model.Pointer;
import com.intellij.platform.backend.documentation.DocumentationResult;
import com.intellij.platform.backend.documentation.DocumentationTarget;
import com.intellij.platform.backend.presentation.TargetPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Field;
import org.moqui.idea.plugin.dom.model.ViewEntity;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class FieldDocumentTarget implements DocumentationTarget {
    final private Field field;
    final private PsiElement element;

    final private String fieldName;
    FieldDocumentTarget(@NotNull PsiElement element){

        this.element = element;
        this.field = MyDomUtils.getLocalDomElementByPsiElement(element,Field.class).orElse(null);
        if(this.field !=null) {
            this.fieldName = MyDomUtils.getXmlAttributeValueString(this.field.getName()).orElse(MyStringUtils.EMPTY_STRING);
        }else {
            this.fieldName = MyStringUtils.EMPTY_STRING;
        }
    }
    @NotNull
    @Override
    public Pointer<? extends DocumentationTarget> createPointer() {
        return Pointer.hardPointer(this);
    }

//    @Nullable
//    @Override
//    public Navigatable getNavigatable() {
//        return DocumentationTarget.super.getNavigatable();
//    }

    @Nullable
    @Override
    public DocumentationResult computeDocumentation() {
        StringBuilder stringBuilder = new StringBuilder();
        if(this.field == null) {
            stringBuilder.append("<h1>");
            stringBuilder.append("Field is not found.");
            stringBuilder.append("</h1>");
        }else {
        }
        stringBuilder.append("<p>");
        stringBuilder.append("<b>");
        stringBuilder.append(this.fieldName);
        stringBuilder.append("</p>");
        stringBuilder.append("</b>");
        stringBuilder.append("<hr>");
        stringBuilder.append("<pre>");
        //.replaceAll("\n|\r\n|\r", "<br>")

        String content = field.getXmlElement().getText();

        if(content != null) {
            content = MyStringUtils.formatXmlForHtml(content);
        }else {
            content = "No content";
        }

        stringBuilder.append(content);
        stringBuilder.append("</pre>");
        return DocumentationResult.documentation(stringBuilder.toString());

    }

    @NotNull
    @Override
    public TargetPresentation computePresentation() {
        return TargetPresentation.builder(this.fieldName).presentation();
    }

    @Nullable
    @Override
    public String computeDocumentationHint() {
        return DocumentationTarget.super.computeDocumentationHint();
    }
}
