package org.moqui.idea.plugin.reference;


import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;

public class PsiRef extends PsiReferenceBase.Immediate<PsiElement> {

    private final TextRange myTextRange;
//    private final PsiElement myResolve;
    public PsiRef(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve) {
        super(psiElement, textRange,myResolve);

        this.myTextRange = textRange;
//        this.myResolve = myResolve;
    }

//    @NotNull
//    @Override
//    public String getCanonicalText() {
//        return MyStringUtils.lowerCaseFirstChar(this.myTextRange.substring(this.myElement.getText()));
//    }
//
//
//    @Override
//    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
//        return ElementManipulators.getManipulator(this.myElement).handleContentChange(this.myElement,this.myTextRange,newElementName);
//    }
}
