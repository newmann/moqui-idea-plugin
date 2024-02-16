package org.moqui.idea.plugin.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;

import java.util.Optional;

/**
 *
 */
public class EntitiesFilesInspection extends MoquiXmlFilesInspection {


    @Override
    public @Nullable @Nls String getStaticDescription() {
        return "Check Entity files";
    }

    @Override
    protected void checkDomElement(@NotNull DomElement element, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        super.checkDomElement(element, holder, helper);
        final Project project = element.getXmlElement().getProject();
        final PsiFile file = element.getXmlElement().getContainingFile();
        if(!EntityUtils.isEntitiesFile(file)) return;

//        System.out.println(element.getClass().getName());

        if ((element instanceof Relationship relationship)) {
            checkRelationshipTag(relationship,holder,helper);
            return;
        }

        if ((element instanceof MemberEntity memberEntity)) {
            checkMemberEntityTag(memberEntity,holder,helper);
            return;
        }

        if(element instanceof ExtendEntity extendEntity) {
            checkExtendEntityTag(extendEntity,holder,helper);
            return;
        }


    }

    private void checkRelationshipTag(@NotNull Relationship relationship, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        GenericAttributeValue attributeValue = relationship.getRelated();
        EntityUtils.inspectEntityFromAttribute(attributeValue,holder,helper);

    }
    private void checkMemberEntityTag(@NotNull MemberEntity memberEntity, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        GenericAttributeValue attributeValue = memberEntity.getEntityName();
        EntityUtils.inspectEntityFromAttribute(attributeValue,holder,helper);

    }
    private void checkExtendEntityTag(@NotNull ExtendEntity extendEntity, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        String entityName = extendEntity.getEntityName().getValue();
        String packageName = extendEntity.getPackage().getValue();

        Optional<XmlElement> optionalXmlElement = EntityUtils.findEntityAndViewEntityXmlElementByNameAndPackage(
                extendEntity.getXmlTag().getProject(), entityName,packageName);

        if (optionalXmlElement.isEmpty()) {
            int start = extendEntity.getXmlTag().getTextOffset();
            int length = extendEntity.getXmlTag().getLocalName().length();

            holder.createProblem(extendEntity, ProblemHighlightType.ERROR,"Entity is not found",
                    TextRange.from(1, length));
        }

    }


}
