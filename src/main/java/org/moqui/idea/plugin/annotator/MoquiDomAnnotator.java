package org.moqui.idea.plugin.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomElementsAnnotator;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.*;

import java.util.Optional;

public class MoquiDomAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        Optional<PsiFile> psiFileOptional = MyDomUtils.getPsiFileFromPsiElement(psiElement);
        if (psiFileOptional.isEmpty() ) return;
        if(!(psiFileOptional.get() instanceof XmlFile xmlFile)) return;
        if(!MyDomUtils.isMoquiXmlFile(xmlFile)) return;

        if(! (psiElement instanceof XmlTag xmlTag)) return;
        DomElement domElement = DomManager.getDomManager(psiElement.getProject()).getDomElement(xmlTag);
        if(domElement == null) return;

        processAnnotate(domElement,annotationHolder);
    }



    private  void processAnnotate(DomElement element, AnnotationHolder holder) {
//        XmlFile xmlFile = DomUtil.getFile(element);
//        if(!MyDomUtils.isMoquiXmlFile(xmlFile)) return;

        LocationUtils.inspectLocationFromDomElement(element,holder);

        if (element instanceof ServiceCall serviceCall) {
            ServiceUtils.inspectServiceCallFromAttribute(serviceCall.getName(), holder);
            return;
        }

        if ((element instanceof Relationship relationship)) {

            EntityUtils.inspectEntityFromAttribute(relationship.getRelated(),holder);
            return;
        }

        if ((element instanceof MemberEntity memberEntity)) {
            EntityUtils.inspectEntityFromAttribute(memberEntity.getEntityName(),holder);
            return;
        }

        if(element instanceof ExtendEntity extendEntity) {
            EntityUtils.inspectExtendEntity(extendEntity, holder);
            return;
        }
        if ((element instanceof Eeca eeca)) {
            EntityUtils.inspectEntityFromAttribute(eeca.getEntity(),holder);
            return;
        }

        if ((element instanceof AbstractEntityName abstractEntityName)) {
            EntityUtils.inspectEntityFromAttribute(abstractEntityName.getEntityName(),holder);
            return;
        }
        if(element instanceof TransitionInclude transitionInclude) {
            ScreenUtils.inspectTransitionInclude(transitionInclude, holder);
            return;
        }

        if ((element instanceof Seca seca)) {
            ServiceUtils.inspectServiceCallFromAttribute(seca.getService(), holder);
        }


    }
}
