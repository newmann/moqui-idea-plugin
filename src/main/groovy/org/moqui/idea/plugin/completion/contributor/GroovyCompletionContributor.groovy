package org.moqui.idea.plugin.completion.contributor

import com.intellij.codeInsight.completion.CompletionType
import static org.moqui.idea.plugin.project.pattern.MoquiGroovyPatterns.ENTITY_CALL

class GroovyCompletionContributor extends MoquiBaseCompletionContributor{
    GroovyCompletionContributor(){
        this.extend(CompletionType.BASIC,ENTITY_CALL,entityOrViewNameCompletionProvider)
    }
}
