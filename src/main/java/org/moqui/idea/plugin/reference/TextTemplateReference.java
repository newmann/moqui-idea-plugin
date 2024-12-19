package org.moqui.idea.plugin.reference;


import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.IncorrectOperationException;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.FieldDescriptor;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 支持在label，text的属性中自动显示内容模板
 */
public class TextTemplateReference extends PsiReferenceBase.Immediate<PsiElement> {

    public static TextTemplateReference of(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve){
        return new TextTemplateReference(psiElement,textRange,myResolve);
    }
    private final Logger LOG = Logger.getInstance(TextTemplateReference.class);

    private final TextRange myTextRange;
//    private final PsiElement myResolve;
    public TextTemplateReference(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve) {
        super(psiElement, textRange,myResolve);

        this.myTextRange = textRange;
//        this.myResolve = myResolve;
    }

    @Override
    public @NotNull Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();


//        List<IndexAbstractField> indexAbstractFieldList = EntityUtils.getIndexAbstractFieldListByPsiElement(myElement);
//
//        indexAbstractFieldList.forEach(item ->{
//            String fieldName = MyDomUtils.getValueOrEmptyString(item.getName());
//            if(!inputedFields.contains(fieldName)) {
//                variants.add(
//                        LookupElementBuilder.create(fieldName)
//                                .withCaseSensitivity(false)
//                                .withTailText(MyStringUtils.formatFieldNameTrailText(MyDomUtils.getValueOrEmptyString(item.getType())))
//                                .withTypeText(item.getInAbstractIndexEntity() == null? "N/A": item.getInAbstractIndexEntity().getShortName())
//                                .withIcon(item.getInAbstractIndexEntity() instanceof IndexEntity ? MoquiIcons.EntityTag: MoquiIcons.ViewEntityTag)
//                );
//            }
//        });

        return variants.toArray();
    }



    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return ElementManipulators.getManipulator(this.myElement).handleContentChange(this.myElement,this.myTextRange,newElementName);
    }
}
