package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.AbstractTransition;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.dom.model.TransitionInclude;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.LocationUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.*;
import java.util.stream.Collectors;


public class TransitionIncludeNameConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter<String> {



    @Override
    public @InspectionMessage String getErrorMessage(@Nullable String s, ConvertContext context) {
        if(s==null) {
            return super.getErrorMessage(s, context);
        }else {
            return "找不到[" + s + "]对应的transition定义。";
        }
    }

    @Override
    public @NotNull Collection<String> getVariants(ConvertContext context) {
        List<String> result = new ArrayList<>();
        Optional<TransitionInclude> transitionIncludeOptional = MyDomUtils.getLocalDomElementByConvertContext(context,TransitionInclude.class);
        if(transitionIncludeOptional.isEmpty()) return result;
        Optional<DomFileElement<Screen>> screenOptional = ScreenUtils.getScreenFileByLocation(context.getProject(),
                MyDomUtils.getValueOrEmptyString(transitionIncludeOptional.get().getLocation()));
        if(screenOptional.isEmpty()) return result;

        return ScreenUtils.getAbstractTransitionListFromScreen(screenOptional.get().getRootElement())
                .stream()
                .map(AbstractTransition::getName)
                .map(MyDomUtils::getValueOrEmptyString)
                .collect(Collectors.toSet());
    }

    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        TransitionInclude transitionInclude = MyDomUtils.getLocalDomElementByConvertContext(context,TransitionInclude.class).orElse(null);
        if(transitionInclude == null) return PsiReference.EMPTY_ARRAY;

        String transitionName = value.getStringValue();
        if(MyStringUtils.isEmpty(transitionName)) return PsiReference.EMPTY_ARRAY;
        DomFileElement<Screen> screenDomFile = ScreenUtils.getScreenFileByLocation(context.getProject(),
                MyDomUtils.getValueOrEmptyString(transitionInclude.getLocation())).orElse(null);
        if(screenDomFile == null) return PsiReference.EMPTY_ARRAY;

        List<AbstractTransition> abstractTransitionList = ScreenUtils.getAbstractTransitionListFromScreen(screenDomFile.getRootElement());
        Optional<AbstractTransition> abstractTransitionOptional = ScreenUtils.getAbstractTransitionFromListByName(abstractTransitionList,transitionName);
        PsiReference[] result = new PsiReference[1];
        result[0] = abstractTransitionOptional
                .map(abstractTransition -> new PsiRef(element, element.getTextRange(), abstractTransition.getName().getXmlAttribute()))
                .orElseGet(() -> new PsiRef(element, element.getTextRange(), null));
        return result;
    }


}
