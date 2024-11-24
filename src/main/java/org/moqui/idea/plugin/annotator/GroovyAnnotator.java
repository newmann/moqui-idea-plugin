package org.moqui.idea.plugin.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.contributor.GroovyCodeReferenceContributor;
import org.moqui.idea.plugin.util.BeginAndEndCharPattern;
import org.moqui.idea.plugin.util.EntityUtils;

import java.util.Optional;

public class GroovyAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
//        if(!(element instanceof XmlAttribute attribute)) return;
//        if(!(attribute.getName().equals("entity-name"))) return;
//        if(!(ServiceUtils.isServicesFile(element.getContainingFile()))) return;
        if(GroovyCodeReferenceContributor.ENTITY_FIND_PATTERN.accepts(element)) {
            BeginAndEndCharPattern elementPattern = BeginAndEndCharPattern.of(element);
            final String entityName = elementPattern.getContent();

            Optional<XmlElement> optDomElement = EntityUtils.getEntityOrViewEntityXmlElementByName(element.getProject(), entityName);
            //没有找到对应的entity定义，则显示错误
            if (optDomElement.isEmpty()) {
//                XmlAttributeValue attributeValue = attribute.getValueElement();

                holder.newAnnotation(HighlightSeverity.ERROR, "没有找到Entity定义")
                        .range(element.getTextRange())
                        .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                        .create();
            }
        }



    }
}
