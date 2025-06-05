package org.moqui.idea.plugin.reference;


import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class MoquiBaseReference extends PsiReferenceBase.Immediate<PsiElement> {

    public static MoquiBaseReference of(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve){
        return new MoquiBaseReference(psiElement,textRange,myResolve);
    }

    public static PsiReference[] createNullRefArray(@NotNull PsiElement psiElement, TextRange textRange){
        PsiReference[] psiReferences = new PsiReference[1];
        psiReferences[0] = new MoquiBaseReference(psiElement,
                textRange,
                null);
        return psiReferences;
    }
    public static PsiReference[] createOneRefArray(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve){
        PsiReference[] psiReferences = new PsiReference[1];
        psiReferences[0] = new MoquiBaseReference(psiElement,
                textRange,
                myResolve);
        return psiReferences;
    }

    protected final TextRange myTextRange;
    protected final PsiElement myResolve;

    public MoquiBaseReference(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve) {
        super(psiElement, textRange,myResolve);

        this.myTextRange = textRange;
        this.myResolve = myResolve;
    }

//    @NotNull
//    @Override
//    public String getCanonicalText() {
//        return MyStringUtils.lowerCaseFirstChar(this.myTextRange.substring(this.myElement.getText()));
//    }
//
//
    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return ElementManipulators.getManipulator(this.myElement).handleContentChange(this.myElement,this.myTextRange,newElementName);
    }
}
