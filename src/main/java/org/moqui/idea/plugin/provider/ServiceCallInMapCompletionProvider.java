package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexServiceParameter;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 支持serviceCall的in-map属性
 * “[”字符后面才开始匹配
 * “[" 后面的","才开始匹配，":"后面不匹配
 */
public class ServiceCallInMapCompletionProvider extends CompletionProvider<CompletionParameters> {
    public static ServiceCallInMapCompletionProvider of(){
        return new ServiceCallInMapCompletionProvider();
    }

    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> IN_MAP_PATTERN =
            PlatformPatterns.psiElement().inside(
                    XmlPatterns.xmlAttributeValue(ServiceCall.ATTR_IN_MAP).inside(
                            XmlPatterns.xmlTag().withLocalName(ServiceCall.TAG_NAME).inside(
                                    XmlPatterns.xmlTag().withLocalName(Service.TAG_NAME, Screen.TAG_NAME,Seca.TAG_NAME,Eeca.TAG_NAME)
                            )
                    )            );

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet result) {
        PsiElement psiElement = parameters.getPosition();
        result.restartCompletionWhenNothingMatches();
        result.addAllElements(findCompletionItem(psiElement));

    }


    public List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement) {
        String inputString = psiElement.getText();

        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();

        if(!showAutoCompleteWindow(inputString)) return lookupElementBuilders;


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

    private boolean showAutoCompleteWindow(String inputString){
        String caretFrontString = MyStringUtils.getDummyFrontString(inputString);
        boolean show = false;
        int totalLength = caretFrontString.length();
        int stepIndex = 0;
        while (stepIndex < totalLength){
            switch (caretFrontString.charAt(stepIndex)){
                case '[' ->{show = true;}
                case ']' ->{show = false;}
                case ':' ->{ show = false;}
                case ',' ->{ show = true;}
            }
            stepIndex +=1;
        }

        return show;
    }
}
