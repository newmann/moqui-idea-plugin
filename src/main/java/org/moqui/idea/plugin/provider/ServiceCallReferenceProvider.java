package org.moqui.idea.plugin.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.ServiceUtils;

public class ServiceCallReferenceProvider extends PsiReferenceProvider {
//    private static Logger logger = Logger.getInstance(ServiceCallReferenceProvider.class);
    public static ServiceCallReferenceProvider of(){
        return new ServiceCallReferenceProvider();
    }
    public ServiceCallReferenceProvider() {
        super();
    }

    @Override
    public @NotNull  PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        return ServiceUtils.createServiceCallReferences(psiElement.getProject(),psiElement);

    }
}
