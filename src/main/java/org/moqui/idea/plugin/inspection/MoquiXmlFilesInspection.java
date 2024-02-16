package org.moqui.idea.plugin.inspection;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.ScreenUtils;
import org.moqui.idea.plugin.util.SecaUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

/**
 *
 */
public abstract class MoquiXmlFilesInspection extends BasicDomElementsInspection<DomElement> {


    public MoquiXmlFilesInspection() {
        super(DomElement.class);
    }

//    @Override
//    protected void checkDomElement(@NotNull DomElement element, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
//        super.checkDomElement(element, holder, helper);
//    }

    protected void checkServiceCallTag(@NotNull ServiceCall serviceCall,@NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        GenericAttributeValue attributeValue = serviceCall.getName();
        ServiceUtils.inspectServiceCallFromAttribute(attributeValue, holder,helper);

    }
    protected void checkEntityFindTag(@NotNull EntityFind entityFind, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        GenericAttributeValue attributeValue = entityFind.getEntityName();
        EntityUtils.inspectEntityFromAttribute(attributeValue, holder,helper);

    }

    protected void checkEntityFindOneTag(@NotNull EntityFindOne entityFindOne, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        GenericAttributeValue attributeValue = entityFindOne.getEntityName();
        EntityUtils.inspectEntityFromAttribute(attributeValue, holder,helper);

    }

    protected void checkTransitionIncludeTag(@NotNull TransitionInclude transitionInclude, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        ScreenUtils.inspectTransitionInclude(transitionInclude, holder, helper);

    }

}
