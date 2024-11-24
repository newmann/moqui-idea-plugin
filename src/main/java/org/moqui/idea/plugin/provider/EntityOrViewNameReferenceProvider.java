package org.moqui.idea.plugin.provider;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.EntityScope;
import org.moqui.idea.plugin.util.EntityUtils;

public class EntityOrViewNameReferenceProvider extends PsiReferenceProvider {
    private static Logger logger = Logger.getInstance(EntityOrViewNameReferenceProvider.class);
    public static EntityOrViewNameReferenceProvider of(@NotNull EntityScope entityScope){
        return new EntityOrViewNameReferenceProvider(entityScope);
    }

    private EntityScope myEntityScope;
    public EntityOrViewNameReferenceProvider(@NotNull EntityScope entityScope) {
        myEntityScope = entityScope;
    }

    @Override
    public @NotNull  PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        return EntityUtils.createEntityOrViewNameReferences(psiElement.getProject(),psiElement,myEntityScope);


    }


}
