package org.moqui.idea.plugin.completion.contributor

import com.intellij.codeInsight.completion.CompletionType

import static org.moqui.idea.plugin.project.pattern.MoquiJavaPatterns.ENTITY_CALL

class JavaCompletionContributor extends MoquiBaseCompletionContributor{
    JavaCompletionContributor(){
        this.extend(CompletionType.BASIC,ENTITY_CALL,entityOrViewNameCompletionProvider)
    }
}
