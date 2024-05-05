package org.moqui.idea.plugin.completion.contributor

import com.intellij.codeInsight.completion.CompletionContributor
import org.moqui.idea.plugin.completion.provider.common.EntityOrViewNameCompletionProvider

class MoquiBaseCompletionContributor extends CompletionContributor{
    EntityOrViewNameCompletionProvider entityOrViewNameCompletionProvider

    MoquiBaseCompletionContributor(){
        entityOrViewNameCompletionProvider = new EntityOrViewNameCompletionProvider()
    }
}
