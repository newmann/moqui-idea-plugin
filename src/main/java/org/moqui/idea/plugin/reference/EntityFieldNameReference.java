package org.moqui.idea.plugin.reference;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MyStringUtils;

public class EntityFieldNameReference extends PsiReferenceBase.Immediate<PsiElement> {

    public static EntityFieldNameReference of(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve){
        return new EntityFieldNameReference(psiElement,textRange,myResolve);
    }
    private final Logger LOG = Logger.getInstance(EntityFieldNameReference.class);

    private final TextRange myTextRange;
//    private final PsiElement myResolve;
    public EntityFieldNameReference(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve) {
        super(psiElement, textRange,myResolve);

        this.myTextRange = textRange;
//        this.myResolve = myResolve;
    }

//    @NotNull
//    @Override
//    public String getCanonicalText() {
//        LOG.warn("Source:" + this.myElement.getText() +", TextRange:"+ this.myTextRange.toString() + ", result:" + MyStringUtils.lowerCaseFirstChar(this.myTextRange.substring(this.myElement.getText())));
//        return MyStringUtils.lowerCaseFirstChar(this.myTextRange.substring(this.myElement.getText()));
//    }


    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return ElementManipulators.getManipulator(this.myElement).handleContentChange(this.myElement,this.myTextRange,newElementName);
    }
}
