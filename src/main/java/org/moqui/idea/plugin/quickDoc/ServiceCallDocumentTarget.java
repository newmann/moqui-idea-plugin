package org.moqui.idea.plugin.quickDoc;

import com.intellij.model.Pointer;
import com.intellij.platform.backend.documentation.DocumentationResult;
import com.intellij.platform.backend.documentation.DocumentationTarget;
import com.intellij.platform.backend.presentation.TargetPresentation;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Service;

import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

public class ServiceCallDocumentTarget implements DocumentationTarget {
    final private Service service;
    final private PsiElement element;

    ServiceCallDocumentTarget(@NotNull PsiElement element){

        this.element = element;
        this.service = MyDomUtils.getLocalDomElementByPsiElement(element,Service.class).orElse(null);
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
        if(this.service == null) {
            stringBuilder.append("<h1>");
            stringBuilder.append("Service is not found.");
            stringBuilder.append("</h1>");
        }else {
        }
        stringBuilder.append("<p>");
        stringBuilder.append("<b>");
        stringBuilder.append(ServiceUtils.getFullNameFromService(this.service));
        stringBuilder.append("</b>");
        stringBuilder.append("</p>");
        stringBuilder.append("<hr>");
        stringBuilder.append("<pre>");
        //.replaceAll("\n|\r\n|\r", "<br>")
        String content = service.getXmlElement().getText();

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
        return TargetPresentation.builder(ServiceUtils.getFullNameFromService(service)).presentation();
    }

    @Nullable
    @Override
    public String computeDocumentationHint() {
        return DocumentationTarget.super.computeDocumentationHint();
    }
}
