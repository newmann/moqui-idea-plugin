package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.AbstractTransition;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.LocationUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 1、指向本screen的transition.name
 * 1、指向某个screen文件：<dynamic-container id="FieldListContainer" transition="FieldList"/>
 */
public class TransitionConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter<String> {
//    @Override
//    public @Nullable AbstractTransition fromString(@Nullable @NonNls String s, ConvertContext context) {
//        if(s == null) return null;
//        return getTransition(s,context)
//                .orElse(null);
//
//    }


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
        return getTransitionList(context).stream().map(AbstractTransition::getName)
                .map(MyDomUtils::getValueOrEmptyString).collect(Collectors.toSet());
    }

//    @Override
//    public @Nullable String toString(@Nullable AbstractTransition transition, ConvertContext context) {
//        if (transition == null) return null;
//        return MyDomUtils.getValueOrEmptyString(transition.getName());
//    }

//    @Override
//    public @Nullable LookupElement createLookupElement(AbstractTransition transition) {
//        if(transition == null) {
//            return super.createLookupElement(transition);
//        }else {
//            String s = MyDomUtils.getValueOrEmptyString(transition.getName());
//            return LookupElementBuilder.create(s)
//                    .withCaseSensitivity(false);
//
//        }
//    }

//    @Override
//    public @Nullable PsiElement getPsiElement(@Nullable AbstractTransition resolvedValue) {
//        return super.getPsiElement(resolvedValue);
//    }

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        return LocationUtils.createReferences(value,element,context);
//        String related = value.getStringValue();
//        if (related == null) return PsiReference.EMPTY_ARRAY;
//        if(related.trim().equals(".")) return PsiReference.EMPTY_ARRAY; //指向当前form，无需转跳
//        PsiRef psiRef;
//        Optional<AbstractTransition> optTransition = getTransition(related,context);
//        if (optTransition.isEmpty()) {
//            psiRef = new PsiRef(element,
//                    new TextRange(1,
//                            related.length() + 1),
//                    null);//用于报错
//
//        }else {
//            final AbstractTransition transition = optTransition.get();
//
//            psiRef = new PsiRef(element,
//                    new TextRange(1,
//                            related.length() + 1),
//                    transition.getName().getXmlAttributeValue());
//        }
//        PsiReference[] result = new PsiReference[1];
//        result[0] = psiRef;
//
//        return result;

    }
    /**
     * 根据当前位置找到所有可用的Transition
     *
     * @param context
     * @return
     */
    private List<AbstractTransition> getTransitionList(ConvertContext context) {

        List<AbstractTransition> result = new ArrayList<AbstractTransition>();


        Screen screen = ScreenUtils.getCurrentScreen(context).orElse(null);
        if(screen != null){
            result.addAll(screen.getTransitionList());
            result.addAll(screen.getTransitionIncludeList());
        }
        return result;
    }
    /**
     * 根据当前位置对应的Transition
     * @param related
     * @param context
     * @return
     */
    private Optional<AbstractTransition> getTransition(String related, ConvertContext context) {
        List<AbstractTransition> transitionList = getTransitionList(context);
        return transitionList.stream().filter(
                item->{
                    String str = MyDomUtils.getValueOrEmptyString(item.getName());
                    return str.equals(related);
                    }
        ).findFirst();

    }


}
