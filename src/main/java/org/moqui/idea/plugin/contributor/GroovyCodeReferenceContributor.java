package org.moqui.idea.plugin.contributor;

import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.provider.EntityOrViewNameReferenceProvider;

public class GroovyCodeReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(GroovyCodeCompletionContributor.ENTITY_CALL, new EntityOrViewNameReferenceProvider());
    }
}
