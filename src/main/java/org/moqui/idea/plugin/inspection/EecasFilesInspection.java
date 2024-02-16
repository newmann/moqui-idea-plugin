package org.moqui.idea.plugin.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Eeca;
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.util.EecaUtils;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.Optional;

/**
 * 现在发现几点：
 * 1、Inspection在文件打开的时候执行一次
 * 2、在运行菜单：Code|Inspect Code时，执行
 *
 */
public class EecasFilesInspection extends MoquiXmlFilesInspection {

    @Override
    public @Nullable @Nls String getStaticDescription() {
        return "Check Eecas files";
    }

    @Override
    protected void checkDomElement(@NotNull DomElement element, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        super.checkDomElement(element, holder, helper);
        final Project project = element.getXmlElement().getProject();
        final PsiFile file = element.getXmlElement().getContainingFile();
        if(!EecaUtils.isEecasFile(file)) return;

//        System.out.println(element.getClass().getName());

        if ((element instanceof Eeca eeca)) {
            checkEecaTag(eeca,holder,helper);
            return;
        }

        if(element instanceof ServiceCall serviceCall) {
            checkServiceCallTag(serviceCall,holder,helper);
            return;
        }


    }

    private void checkEecaTag(@NotNull Eeca eeca,@NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        GenericAttributeValue attributeValue = eeca.getEntity();
        EntityUtils.inspectEntityFromAttribute(attributeValue,holder,helper);


    }

}
