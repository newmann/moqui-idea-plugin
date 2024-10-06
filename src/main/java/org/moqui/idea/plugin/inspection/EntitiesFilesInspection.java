package org.moqui.idea.plugin.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;

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
//        super.checkDomElement(element, holder, helper);
//        final Project project = element.getXmlElement().getProject();

        XmlElement xmlElement = element.getXmlElement();
        if(xmlElement == null) return;

        final PsiFile file = xmlElement.getContainingFile();
        if(!EntityUtils.isEntitiesFile(file)) return;

//        System.out.println(element.getClass().getName());

        if ((element instanceof Relationship relationship)) {
            checkRelationshipTag(relationship,holder);
            return;
        }

        if ((element instanceof MemberEntity memberEntity)) {
            checkMemberEntityTag(memberEntity,holder);
            return;
        }

        if(element instanceof ExtendEntity extendEntity) {
            checkExtendEntityTag(extendEntity,holder,helper);
            return;
        }


    }

    private void checkRelationshipTag(@NotNull Relationship relationship, @NotNull DomElementAnnotationHolder holder) {
        GenericAttributeValue<String> attributeValue = relationship.getRelated();
        EntityUtils.inspectEntityFromAttribute(attributeValue,holder);

    }
    private void checkMemberEntityTag(@NotNull MemberEntity memberEntity, @NotNull DomElementAnnotationHolder holder) {
        GenericAttributeValue<String> attributeValue = memberEntity.getEntityName();
        EntityUtils.inspectEntityFromAttribute(attributeValue,holder);

    }
    private void checkExtendEntityTag(@NotNull ExtendEntity extendEntity, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        String entityName = MyDomUtils.getValueOrEmptyString(extendEntity.getEntityName());
        XmlTag xmlTag = extendEntity.getXmlTag();
        if(xmlTag == null) {return;}

        Optional<XmlElement> optionalXmlElement = EntityUtils.getEntityOrViewEntityXmlElementByName(
               xmlTag.getProject(), entityName);

        if (optionalXmlElement.isEmpty()) {
            int start = xmlTag.getTextOffset();
            int length = xmlTag.getLocalName().length();

            holder.createProblem(extendEntity, ProblemHighlightType.ERROR,"Entity is not found",
                    TextRange.from(1, length));
        }

    }


}
