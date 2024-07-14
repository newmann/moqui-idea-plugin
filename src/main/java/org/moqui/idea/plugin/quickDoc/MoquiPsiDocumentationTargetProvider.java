package org.moqui.idea.plugin.quickDoc;

import com.intellij.platform.backend.documentation.DocumentationTarget;
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class MoquiPsiDocumentationTargetProvider implements PsiDocumentationTargetProvider {
    /**
     * element，如果当前元素有psiReference，则是指向的element
     * originalElement是当前的element
     * @param element @NotNull PsiElement
     * @param originalElement @NotNull PsiElement
     * @return @Nullable DocumentationTarget
     */
    @Override
    public @Nullable DocumentationTarget documentationTarget(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
        PsiFile psiFile = element.getContainingFile();

        if(psiFile == null) return null;

        if(MyDomUtils.isNotMoquiXmlFile(psiFile)) return null;

        if(MyDomUtils.isNotAttributeValue(element)) return null;


        final String tagName = MyDomUtils.getCurrentTagName(element).orElse(MyStringUtils.EMPTY_STRING);
        return switch (tagName) {
            case Entity.TAG_NAME -> new EntityDocumentTarget(element);
            case ViewEntity.TAG_NAME -> new ViewEntityDocumentTarget(element);
            case Service.TAG_NAME -> new ServiceCallDocumentTarget(element);
            case Field.TAG_NAME -> new FieldDocumentTarget(element);
            case Relationship.TAG_NAME -> new RelationshipDocumentTarget(element);

            default -> null;
        };


    }


}
