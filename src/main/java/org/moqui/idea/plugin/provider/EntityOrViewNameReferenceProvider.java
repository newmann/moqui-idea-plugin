package org.moqui.idea.plugin.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.EntityUtils;

public class EntityOrViewNameReferenceProvider extends PsiReferenceProvider {
    @Override
    public @NotNull  PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        String name = psiElement.getText();
        if(name.charAt(0) == '"') name = name.substring(1);
        if(name.endsWith("\"")) name = name.substring(0,name.length()-1);
        if(name.isBlank()){
            return new PsiReference[0];
        }else {
            return EntityUtils.createEntityNameReferences(psiElement.getProject(), psiElement, name, 1);
        }
    }
}
