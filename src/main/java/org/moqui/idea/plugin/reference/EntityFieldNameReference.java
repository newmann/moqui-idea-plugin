package org.moqui.idea.plugin.reference;


import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public @NotNull Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();

        List<IndexAbstractField> indexAbstractFieldList = EntityUtils.getIndexAbstractFieldListByPsiElement(myElement);

        addLookupElement(indexAbstractFieldList,variants);

        return variants.toArray();
    }

    private void addLookupElement(@NotNull List<IndexAbstractField> indexAbstractFieldList, @NotNull List<LookupElement> lookupList){
        indexAbstractFieldList.forEach(item->{

            lookupList.add(LookupElementBuilder.create(item.getName())
                    .withCaseSensitivity(false)
                    .withIcon(item.getInAbstractIndexEntity() instanceof IndexEntity ? MoquiIcons.EntityTag: MoquiIcons.ViewEntityTag)
                    .withTypeText(item.getInAbstractIndexEntity() == null? "N/A": item.getInAbstractIndexEntity().getShortName()));
        });

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
