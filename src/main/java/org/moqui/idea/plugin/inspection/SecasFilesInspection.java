package org.moqui.idea.plugin.inspection;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Seca;
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.util.SecaUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

/**
 *
 */
public class SecasFilesInspection extends MoquiXmlFilesInspection {


    @Override
    public @Nullable @Nls String getStaticDescription() {
        return "Check Secas files";
    }

    @Override
    protected void checkDomElement(@NotNull DomElement element, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
//        super.checkDomElement(element, holder, helper);
        final Project project = element.getXmlElement().getProject();
        final PsiFile file = element.getXmlElement().getContainingFile();
        if(!SecaUtils.isSecasFile(file)) return;

//        System.out.println(element.getClass().getName());

        if ((element instanceof Seca seca)) {
            checkSecaTag(seca,holder);
            return;
        }

        if(element instanceof ServiceCall serviceCall) {
            checkServiceCallTag(serviceCall,holder);
            return;
        }


    }

    private void checkSecaTag(@NotNull Seca seca, @NotNull DomElementAnnotationHolder holder) {
        GenericAttributeValue attributeValue = seca.getService();
        ServiceUtils.inspectServiceCallFromAttribute(attributeValue, holder);



    }



}
