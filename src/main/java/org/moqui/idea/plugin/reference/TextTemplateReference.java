package org.moqui.idea.plugin.reference;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * 支持在label，text的属性中自动显示内容模板
 */
public class TextTemplateReference extends MoquiBaseReference{

    public static TextTemplateReference of(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve){
        return new TextTemplateReference(psiElement,textRange,myResolve);
    }
    private final Logger LOG = Logger.getInstance(TextTemplateReference.class);

//    private final TextRange myTextRange;
//    private final PsiElement myResolve;
    public TextTemplateReference(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve) {
        super(psiElement, textRange,myResolve);

//        this.myTextRange = textRange;
//        this.myResolve = myResolve;
    }


//    @Override
//    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
//        return ElementManipulators.getManipulator(this.myElement).handleContentChange(this.myElement,this.myTextRange,newElementName);
//    }
}
