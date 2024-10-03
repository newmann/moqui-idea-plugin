package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.util.ComponentUtils;
import org.moqui.idea.plugin.util.EntityUtils;

import java.util.Map;

public class ServiceCallCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet result) {
        Project project =parameters.getPosition().getProject();
        addEntitiesLookup(project, result);

    }
    static void addEntitiesLookup(@NotNull Project project, @NotNull CompletionResultSet result){
//        MoquiIndexService entityPsiElementService = project.getService(MoquiIndexService.class);
//
//        Map<String, DomElement> entitySet = entityPsiElementService.getAllEntityDomElements();

        Map<String, DomElement> entitySet = EntityUtils.getAllEntityAndViewEntityDomElementMap(project);

        entitySet.forEach ((key,value)->{
            LookupElementBuilder lookupElement = LookupElementBuilder.create(key);

            if (value instanceof Entity) {
                lookupElement.withIcon(EntityUtils.getNagavitorToEntityIcon());
            }else {
                lookupElement.withIcon(EntityUtils.getNagavitorToViewIcon());
            }

            lookupElement.withTailText(" Component:"+ ComponentUtils.getComponentName(value), true);

            result.addElement(PrioritizedLookupElement.withPriority(lookupElement,100));

        });

    }
}
