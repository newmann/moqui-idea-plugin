package org.moqui.idea.plugin.completion.provider.common

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.intellij.util.xml.DomElement
import org.jetbrains.annotations.NotNull
import org.moqui.idea.plugin.dom.model.Entity
import org.moqui.idea.plugin.service.MoquiIndexService
import org.moqui.idea.plugin.util.ComponentUtils
import org.moqui.idea.plugin.util.EntityUtils

class EntityOrViewNameCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        Project project =parameters.getPosition().getProject();
        addEntitiesLookup(project, result)

    }
    static void addEntitiesLookup(@NotNull Project project, @NotNull CompletionResultSet result){
//        MoquiIndexService entityPsiElementService = project.getService(MoquiIndexService.class);
//
//        Map<String, DomElement> entitySet = entityPsiElementService.getAllEntityDomElements();

        Map<String, DomElement> entitySet = EntityUtils.getAllEntityAndViewEntityDomElementMap(project);

        entitySet.forEach ((key,value)->{
            LookupElement lookupElement = LookupElementBuilder.create(key);

            if (value instanceof Entity) {
                lookupElement.withIcon(EntityUtils.getNagavitorToEntityIcon())
            }else {
                lookupElement.withIcon(EntityUtils.getNagavitorToViewIcon())
            }

            lookupElement.withTailText(" Component:${ComponentUtils.getComponentName(value)}" as String, true)

            result.addElement(PrioritizedLookupElement.withPriority(lookupElement,100))

        })
    }
}
