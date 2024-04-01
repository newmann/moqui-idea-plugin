package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class TransitionConverter extends ResolvingConverter<AbstractTransition> implements CustomReferenceConverter {
    @Override
    public @Nullable AbstractTransition fromString(@Nullable @NonNls String s, ConvertContext context) {
        if(s == null) return null;
        return getTransition(s,context)
                .orElse(null);

    }

    @Override
    public @NotNull Collection<? extends AbstractTransition> getVariants(ConvertContext context) {
        return getTransitionList(context);
    }

    @Override
    public @Nullable String toString(@Nullable AbstractTransition transition, ConvertContext context) {
        if (transition == null) return null;
        return transition.getName().getXmlAttributeValue().getValue();
    }

    @Override
    public @Nullable LookupElement createLookupElement(AbstractTransition transition) {
        if(transition == null) {
            return super.createLookupElement(transition);
        }else {
            String s = transition.getName().getXmlAttributeValue().getValue();
            return LookupElementBuilder.create(s)
                    .withCaseSensitivity(false);

        }
    }

    @Override
    public @Nullable PsiElement getPsiElement(@Nullable AbstractTransition resolvedValue) {
        return super.getPsiElement(resolvedValue);
    }

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        String related = value.getStringValue();
        if (related == null) return PsiReference.EMPTY_ARRAY;

        Optional<AbstractTransition> optTransition = getTransition(related,context);
        if (optTransition.isEmpty()) return PsiReference.EMPTY_ARRAY;

        final AbstractTransition transition = optTransition.get();
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
                    String str = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                            .orElse(MyStringUtils.EMPTY_STRING);
                    return str.equals(related);
                    }
        ).findFirst();

    }


}
