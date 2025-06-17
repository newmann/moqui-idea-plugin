package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class FieldRefCompletionProvider extends CompletionProvider<CompletionParameters> {
//    private final  static Logger LOGGER = Logger.getInstance(ServiceCallCompletionProvider.class);
    public static FieldRefCompletionProvider of(){
        return new FieldRefCompletionProvider() ;
    }
    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> FIELD_REF_NAME_PATTERN =
            PlatformPatterns.psiElement()
                    .inside(
                            XmlPatterns.xmlAttributeValue().andOr(
                                XmlPatterns.xmlAttributeValue(FieldRef.ATTR_NAME)
                                        .inside(
                                                XmlPatterns.xmlTag().withLocalName(FieldRef.TAG_NAME)
                                        ),
                                XmlPatterns.xmlAttributeValue(FormSingle.ATTR_FOCUS_FIELD)
                                        .inside(
                                                XmlPatterns.xmlTag().withLocalName(FormSingle.TAG_NAME)
                                        )
                            )
                    );

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet result) {

        PsiElement psiElement = parameters.getPosition();
        if(! MyDomUtils.isMoquiProject(psiElement.getProject())) return;
        lookupField(psiElement,result);
    }

    private List<AbstractField> getFieldList(PsiElement psiElement) {

        List<AbstractField> result = new ArrayList<>();
        MyDomUtils.getLocalDomElementByPsiElement(psiElement,AbstractForm.class).ifPresent(
                abstractForm -> {result.addAll(ScreenUtils.getFieldListFromForm(abstractForm));}
        );
//        ScreenUtils.getCurrentAbstractForm(psiElement).ifPresent(abstractForm->{
//            result.addAll(ScreenUtils.getFieldListFromForm(abstractForm));
//        });

        return result;
    }
    private void lookupField(@NotNull PsiElement psiElement, @NotNull CompletionResultSet result){

        getFieldList(psiElement).forEach(item->{
            String name = MyDomUtils.getValueOrEmptyString(item.getName());
            Icon icon;

                if (item instanceof Parameter) {
                    icon = MyIcons.ParameterTag;
                }else {
                    if(item instanceof Alias) {
                        icon = MyIcons.ViewEntityTag;
                    }else {
                        icon = MyIcons.ScreenTag;
                    }
                }

            result.addElement(LookupElementBuilder.create(name)
                    .withCaseSensitivity(true)
                    .withIcon(icon));

        });
    }

}
