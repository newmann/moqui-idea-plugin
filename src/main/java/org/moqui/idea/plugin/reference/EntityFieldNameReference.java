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
        List<String> inputedFields = getInputtedFieldSet(myElement);

        List<IndexAbstractField> indexAbstractFieldList = EntityUtils.getIndexAbstractFieldListByPsiElement(myElement);

        indexAbstractFieldList.forEach(item ->{
            String fieldName = MyDomUtils.getValueOrEmptyString(item.getName());
            if(!inputedFields.contains(fieldName)) {
                variants.add(
                        LookupElementBuilder.create(fieldName)
                                .withCaseSensitivity(false)
                                .withTailText(MyStringUtils.formatFieldNameTrailText(MyDomUtils.getValueOrEmptyString(item.getType())))
                                .withTypeText(item.getInAbstractIndexEntity() == null? "N/A": item.getInAbstractIndexEntity().getShortName())
                                .withIcon(item.getInAbstractIndexEntity() instanceof IndexEntity ? MoquiIcons.EntityTag: MoquiIcons.ViewEntityTag)
                );
            }
        });

        return variants.toArray();
    }

//    private void addLookupElement(@NotNull List<IndexAbstractField> indexAbstractFieldList, @NotNull List<LookupElement> lookupList){
//        indexAbstractFieldList.forEach(item->{
//
//            lookupList.add(LookupElementBuilder.create(item.getName())
//                    .withCaseSensitivity(false)
//                    .withTailText(MyStringUtils.formatFieldNameTrailText(MyDomUtils.getValueOrEmptyString(item.getType())))
//                    .withIcon(item.getInAbstractIndexEntity() instanceof IndexEntity ? MoquiIcons.EntityTag: MoquiIcons.ViewEntityTag)
//                    .withTypeText(item.getInAbstractIndexEntity() == null? "N/A": item.getInAbstractIndexEntity().getShortName()));
//        });
//
//    }
    private List<String> getInputtedFieldSet(@NotNull PsiElement psiElement) {
        XmlAttribute xmlAttribute = MyDomUtils.getCurrentAttribute(psiElement).orElse(null);
        if (xmlAttribute == null ) return Collections.emptyList();
        if ( xmlAttribute.getValue() == null) return Collections.emptyList();

        final String inputStr = xmlAttribute.getValue().trim();
        if (inputStr.isEmpty()) return Collections.emptyList();
        List<FieldDescriptor> fieldDescriptorList = EntityUtils.extractFieldDescriptorList(MyStringUtils.removeDummyOnly(inputStr),0);

        return fieldDescriptorList.stream().map(FieldDescriptor::getFieldName).toList();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return ElementManipulators.getManipulator(this.myElement).handleContentChange(this.myElement,this.myTextRange,newElementName);
    }
}
