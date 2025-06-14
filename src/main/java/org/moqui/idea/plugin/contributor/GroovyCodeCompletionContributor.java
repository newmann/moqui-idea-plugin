package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import org.moqui.idea.plugin.provider.ServiceCallCompletionProvider;

public class GroovyCodeCompletionContributor extends CompletionContributor {
    public GroovyCodeCompletionContributor() {
        extend(CompletionType.BASIC,GroovyCodeReferenceContributor.SERVICE_CALL_PATTERN, ServiceCallCompletionProvider.of());
    }
}
