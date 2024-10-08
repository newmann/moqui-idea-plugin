package org.moqui.idea.plugin.reference;


import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
@Deprecated
public class PsiRef extends PsiReferenceBase<PsiElement> {
    private PsiElement psiElement;
    private TextRange textRange;
    private PsiElement myResolve;
    public PsiRef(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve) {
        super(psiElement, textRange);
        this.psiElement = psiElement;
        this.textRange = textRange;
        this.myResolve = myResolve;
    }


    @Override
    public @Nullable PsiElement resolve() {
        return myResolve;
    }
}
