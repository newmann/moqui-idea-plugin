package org.moqui.idea.plugin.reference;


import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.moqui.idea.plugin.util.EntityFacadeXmlUtils.LocalizedMessage_Field_Localized;

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


        EntityFacadeXmlUtils.getAllTextTemplateMap(this.myElement.getProject()).forEach(
                (key, value) -> variants.add(
                    LookupElementBuilder.create(key)
                            .withCaseSensitivity(false)
    //                                        .withTailText(MyStringUtils.formatFieldNameTrailText(MyDomUtils.getValueOrEmptyString(item.getType())))
                            .withTypeText(MyDomUtils.getValueOrEmptyString(value.getAttributeValue(LocalizedMessage_Field_Localized)))
    //                                        .withIcon(item.getInAbstractIndexEntity() instanceof IndexEntity ? MoquiIcons.EntityTag: MoquiIcons.ViewEntityTag)
        ));


        return variants.toArray();
    }



    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return ElementManipulators.getManipulator(this.myElement).handleContentChange(this.myElement,this.myTextRange,newElementName);
    }
}
