package org.moqui.idea.plugin.reference;


import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.service.IndexServiceParameter;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceInParameterReference extends MoquiBaseReference {

    public static ServiceInParameterReference of(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve){
        return new ServiceInParameterReference(psiElement,textRange,myResolve);
    }
    private final Logger LOG = Logger.getInstance(ServiceInParameterReference.class);

//    private final TextRange myTextRange;
//    private final PsiElement myResolve;
    public ServiceInParameterReference(@NotNull PsiElement psiElement, TextRange textRange, PsiElement myResolve) {
        super(psiElement, textRange,myResolve);

//        this.myTextRange = textRange;
//        this.myResolve = myResolve;
    }

    @Override
    public @NotNull Object[] getVariants() {
//        List<LookupElement> variants = new ArrayList<>();
//        variants.addAll(findCompletionItem(myElement));

        return findCompletionItem(myElement).toArray();
    }


    public List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement) {
        String inputString = psiElement.getText();

        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();


        ServiceCall serviceCall= MyDomUtils.getLocalDomElementByPsiElement(psiElement,ServiceCall.class).orElse(null);

        if(serviceCall==null)  return lookupElementBuilders ;

        ProgressManager.checkCanceled();

        final String serviceName = MyDomUtils.getXmlAttributeValueString(serviceCall.getName())
                .orElse(MyStringUtils.EMPTY_STRING);
        if(MyStringUtils.isEmpty(serviceName)) return lookupElementBuilders ;
        ProgressManager.checkCanceled();

        List<String> inputtedFields = getInputtedFieldSet(psiElement);
        List<IndexServiceParameter> fieldList = new ArrayList<>(ServiceUtils.getServiceInParamterList(psiElement.getProject(), serviceName));
        fieldList.forEach(item ->{
            String fieldName = MyDomUtils.getValueOrEmptyString(item.getParameterName());
            if(!inputtedFields.contains(fieldName)) {
                lookupElementBuilders.add(
                        LookupElementBuilder.create(fieldName)
                                .withCaseSensitivity(false)
                                .withTypeText(MyDomUtils.getValueOrEmptyString(item.getType()))
                );
            }
        });

        return lookupElementBuilders;
    }

    private List<String> getInputtedFieldSet(@NotNull PsiElement psiElement) {
        XmlAttribute xmlAttribute = MyDomUtils.getCurrentAttribute(psiElement).orElse(null);
        if (xmlAttribute == null ) return Collections.emptyList();
        if ( xmlAttribute.getValue() == null) return Collections.emptyList();

        final String inputStr = xmlAttribute.getValue().trim();
        if (inputStr.isEmpty()) return Collections.emptyList();
        List<FieldDescriptor> fieldDescriptorList = ServiceUtils.extractMapFieldDescriptorList(MyStringUtils.removeDummyOnly(inputStr),0);

        return fieldDescriptorList.stream().map(FieldDescriptor::getFieldName).toList();
    }


//    @Override
//    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
//        return ElementManipulators.getManipulator(this.myElement).handleContentChange(this.myElement,this.myTextRange,newElementName);
//    }
}
