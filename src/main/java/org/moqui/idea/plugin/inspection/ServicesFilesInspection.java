package org.moqui.idea.plugin.inspection;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.ServiceUtils;

/**
 *
 */
public class ServicesFilesInspection extends MoquiXmlFilesInspection {


    @Override
    public @Nullable @Nls String getStaticDescription() {
        return "Check Services files";
    }

    @Override
    protected void checkDomElement(@NotNull DomElement element, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
//        super.checkDomElement(element, holder, helper);
        final Project project = element.getXmlElement().getProject();
        final PsiFile file = element.getXmlElement().getContainingFile();
        if(!ServiceUtils.isServicesFile(file)) return;

//        System.out.println(element.getClass().getName());

        if ((element instanceof ServiceCall serviceCall)) {
            checkServiceCallTag(serviceCall,holder);
            return;
        }

        if ((element instanceof EntityFindOne entityFindOne)) {
            checkEntityFindOneTag(entityFindOne,holder);
            return;
        }

        if(element instanceof EntityFind entityFind) {
            checkEntityFindTag(entityFind,holder);
            return;
        }


    }





}
