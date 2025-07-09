package org.moqui.idea.plugin.dom.converter;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractTransition;
import org.moqui.idea.plugin.dom.model.TransitionInclude;
import org.moqui.idea.plugin.reference.MoquiBaseReference;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.List;
import java.util.Optional;


public class TransitionIncludeNameReferenceConverter implements CustomReferenceConverter<String> {


    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        String transitionName = value.getStringValue();
        if(MyStringUtils.isEmpty(transitionName)) return PsiReference.EMPTY_ARRAY;
        TextRange textRange= TextRange.create(1,1+transitionName.length());

        TransitionInclude transitionInclude = MyDomUtils.getLocalDomElementByConvertContext(context,TransitionInclude.class).orElse(null);
        if(transitionInclude == null) return PsiReference.EMPTY_ARRAY;

//        DomFileElement<Screen> screenDomFile = ScreenUtils.getScreenFileByLocation(context.getProject(),
//                MyDomUtils.getValueOrEmptyString(transitionInclude.getLocation())).orElse(null);
//        if(screenDomFile == null) return MoquiBaseReference.createNullRefArray(element,textRange);
//
//        List<AbstractTransition> abstractTransitionList = ScreenUtils.getAbstractTransitionListFromScreen(screenDomFile.getRootElement());

        List<AbstractTransition> abstractTransitionList = ScreenUtils.getAbstractTransitionListFromLocation(context.getProject(),
                MyDomUtils.getValueOrEmptyString(transitionInclude.getLocation()));

        Optional<AbstractTransition> abstractTransitionOptional = ScreenUtils.getAbstractTransitionFromListByName(abstractTransitionList,transitionName);
//        PsiReference[] result = new PsiReference[1];
        return abstractTransitionOptional
                .map(abstractTransition ->
                        MoquiBaseReference.createOneRefArray(element, TextRange.create(1,1+transitionName.length()), abstractTransition.getName().getXmlAttributeValue()))
                .orElseGet(() ->
                        MoquiBaseReference.createNullRefArray(element, TextRange.create(1,1+transitionName.length())));

    }


}
