package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.diagnostic.Logger;
import org.moqui.idea.plugin.provider.ServiceCallCompletionProvider;
@Deprecated
public class GroovyCodeCompletionContributor extends CompletionContributor {
    private static Logger LOG = Logger.getInstance(GroovyCodeCompletionContributor.class);

    public GroovyCodeCompletionContributor() {

        extend(CompletionType.BASIC, GroovyCodeReferenceContributor.SERVICE_CALL_PATTERN, new ServiceCallCompletionProvider());
    }

}
