package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class TransitionConverter extends ResolvingConverter<TransitionAbstract> implements CustomReferenceConverter {
    @Override
    public @Nullable TransitionAbstract fromString(@Nullable @NonNls String s, ConvertContext context) {
        if(s == null) return null;
        return getTransition(s,context)
                .orElse(null);

    }

    @Override
    public @NotNull Collection<? extends TransitionAbstract> getVariants(ConvertContext context) {
        return getTransitionList(context);
    }

    @Override
    public @Nullable String toString(@Nullable TransitionAbstract transition, ConvertContext context) {
        if (transition == null) return null;
        return transition.getName().getXmlAttributeValue().getValue();
    }

    @Override
    public @Nullable LookupElement createLookupElement(TransitionAbstract transition) {
        if(transition == null) {
            return super.createLookupElement(transition);
        }else {
            String s = transition.getName().getXmlAttributeValue().getValue();
            return LookupElementBuilder.create(s)
                    .withCaseSensitivity(false);

        }
    }

    @Override
    public @Nullable PsiElement getPsiElement(@Nullable TransitionAbstract resolvedValue) {
        return super.getPsiElement(resolvedValue);
    }

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        String related = value.getStringValue();
        if (related == null) return PsiReference.EMPTY_ARRAY;

        Optional<TransitionAbstract> optTransition = getTransition(related,context);
        if (optTransition.isEmpty()) return PsiReference.EMPTY_ARRAY;

        final TransitionAbstract transition = optTransition.get();
        PsiReference[] psiReferences = new PsiReference[1];
        psiReferences[0] = new PsiRef(element,
                new TextRange(1,
                        related.length()+1),
                transition.getName().getXmlAttributeValue());

        return psiReferences;

    }
    /**
     * 根据当前位置找到所有可用的Transition
     *
     * @param context
     * @return
     */
    private List<TransitionAbstract> getTransitionList(ConvertContext context) {

        List<TransitionAbstract> result = new ArrayList<TransitionAbstract>();


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
    private Optional<TransitionAbstract> getTransition(String related, ConvertContext context) {
        List<TransitionAbstract> transitionList = getTransitionList(context);
        return transitionList.stream().filter(
                item->{
                    String str = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                            .orElse(MyStringUtils.EMPTY_STRING);
                    return str.equals(related);
                    }
        ).findFirst();

    }


}
