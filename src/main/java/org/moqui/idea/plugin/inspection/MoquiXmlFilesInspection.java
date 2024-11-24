package org.moqui.idea.plugin.inspection;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.EntityFind;
import org.moqui.idea.plugin.dom.model.EntityFindOne;
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.dom.model.TransitionInclude;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.ScreenUtils;
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

    protected void checkServiceCallTag(@NotNull ServiceCall serviceCall,@NotNull DomElementAnnotationHolder holder) {
        GenericAttributeValue<String> attributeValue = serviceCall.getName();
        ServiceUtils.inspectServiceCallFromAttribute(attributeValue, holder);

    }
    protected void checkEntityFindTag(@NotNull EntityFind entityFind, @NotNull DomElementAnnotationHolder holder) {
        GenericAttributeValue<String> attributeValue = entityFind.getEntityName();
        EntityUtils.inspectEntityFromAttribute(attributeValue, holder);

    }

    protected void checkEntityFindOneTag(@NotNull EntityFindOne entityFindOne, @NotNull DomElementAnnotationHolder holder) {
        GenericAttributeValue<String> attributeValue = entityFindOne.getEntityName();
        EntityUtils.inspectEntityFromAttribute(attributeValue, holder);

    }

    protected void checkTransitionIncludeTag(@NotNull TransitionInclude transitionInclude, @NotNull DomElementAnnotationHolder holder) {
        ScreenUtils.inspectTransitionInclude(transitionInclude, holder);

    }

}
